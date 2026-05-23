package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FinanceRepository
import com.example.data.SavingsGoal
import com.example.data.Transaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlowTrackViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    private val prefs = application.getSharedPreferences("flowtrack_prefs", android.content.Context.MODE_PRIVATE)
    private val _isMockMode = MutableStateFlow(prefs.getBoolean("is_mock_mode", true))
    val isMockMode = _isMockMode.asStateFlow()

    val allTransactions: StateFlow<List<Transaction>>
    val allGoals: StateFlow<List<SavingsGoal>>

    // Local filter state for UI
    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<String?>(null) // "INCOME", "EXPENSE", null
    val selectedTypeFilter = _selectedTypeFilter.asStateFlow()

    // Monthly Budget limit
    private val _monthlyBudget = MutableStateFlow(45000.0)
    val monthlyBudget = _monthlyBudget.asStateFlow()

    // Screen State holding transaction selected for editing
    private val _editingTransaction = MutableStateFlow<Transaction?>(null)
    val editingTransaction = _editingTransaction.asStateFlow()

    // Dashboard Time Range or Period Filter style
    private val _currentMonthOffset = MutableStateFlow(0) // 0 for current month

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FinanceRepository(db)

        // Run seed check ONLY if mock mode is enabled on starts
        viewModelScope.launch {
            if (_isMockMode.value) {
                repository.checkAndSeedDatabase()
            }
        }

        allTransactions = repository.allTransactions
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        allGoals = repository.allGoals
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // --- RECURSIVE OR COMBINED FILTERS ---
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        allTransactions, _selectedCategoryFilter, _selectedTypeFilter, _searchQuery
    ) { list, category, type, query ->
        list.filter { transaction ->
            val matchCategory = category == null || transaction.category.equals(category, ignoreCase = true)
            val matchType = type == null || transaction.type == type
            val matchQuery = query.isEmpty() || transaction.note.contains(query, ignoreCase = true) ||
                    transaction.category.contains(query, ignoreCase = true) ||
                    transaction.paymentMethod.contains(query, ignoreCase = true) ||
                    transaction.amount.toString().contains(query)
            matchCategory && matchType && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- FINANCIAL METRICS (CALCULATED LOCALLY & REACTIVELY) ---
    val totalBalance: StateFlow<Double> = allTransactions.map { list ->
        val incomes = list.filter { it.type == "INCOME" }.sumOf { it.amount }
        val expenses = list.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        incomes - expenses
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlyIncome: StateFlow<Double> = allTransactions.map { list ->
        list.filter { it.type == "INCOME" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlyExpense: StateFlow<Double> = allTransactions.map { list ->
        list.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val dailyAverageSpending: StateFlow<Double> = allTransactions.map { list ->
        val expenses = list.filter { it.type == "EXPENSE" }
        if (expenses.isEmpty()) return@map 0.0
        val sum = expenses.sumOf { it.amount }
        // find active span of days or assume 10 days for mock data
        val dates = expenses.map { it.date }
        val minDate = dates.minOrNull() ?: System.currentTimeMillis()
        val maxDate = dates.maxOrNull() ?: System.currentTimeMillis()
        val daysDiff = ((maxDate - minDate) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
        sum / daysDiff
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val topCategory: StateFlow<Pair<String, Double>> = allTransactions.map { list ->
        val expenses = list.filter { it.type == "EXPENSE" }
        if (expenses.isEmpty()) return@map Pair("None", 0.0)
        val grouped = expenses.groupBy { it.category }
        val categorySums = grouped.mapValues { entry -> entry.value.sumOf { it.amount } }
        val top = categorySums.maxByOrNull { it.value }
        top?.toPair() ?: Pair("None", 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair("None", 0.0))

    // --- GOALS METRICS ---
    val goalsCompletionRate: StateFlow<Double> = allGoals.map { list ->
        if (list.isEmpty()) return@map 0.0
        val target = list.sumOf { it.targetAmount }
        val saved = list.sumOf { it.savedAmount }
        if (target == 0.0) return@map 0.0
        (saved / target) * 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- DYNAMIC LOCAL INSIGHTS ENGINE (NO AI) ---
    val financialInsights: StateFlow<List<String>> = combine(
        allTransactions, allGoals, _monthlyBudget
    ) { transactionsList, goalsList, budgetLimit ->
        val insightsList = mutableListOf<String>()

        // 1. Budget insight
        val expSum = transactionsList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        if (expSum > budgetLimit) {
            insightsList.add("⚠️ Strict warning! Expense rate has exceeded the ₹${String.format("%,.0f", budgetLimit)} budget limit by ₹${String.format("%,.0f", expSum - budgetLimit)}.")
        } else {
            val percentage = if (budgetLimit == 0.0) 0.0 else (expSum / budgetLimit) * 100
            insightsList.add("💡 Budget health is steady. You have utilized ${String.format("%.1f", percentage)}% of your ₹${String.format("%,.0f", budgetLimit)} limit.")
        }

        // 2. Spending insights
        val groupedCat = transactionsList.filter { it.type == "EXPENSE" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
        val topCat = groupedCat.maxByOrNull { it.value }
        if (topCat != null) {
            insightsList.add("👑 Category focus: Your highest expenditure is on \"${topCat.key}\" totaling ₹${String.format("%,.0f", topCat.value)}.")
        }

        // 3. Saving metrics
        val incomes = transactionsList.filter { it.type == "INCOME" }.sumOf { it.amount }
        if (incomes > 0.0) {
            val savingRate = ((incomes - expSum) / incomes) * 100
            if (savingRate > 0) {
                insightsList.add("🌟 Excellent! This cycle your effective savings rate is ${String.format("%.0f", savingRate)}%. You are retaining a solid portion of your earnings.")
            } else {
                insightsList.add("📉 Warning: Your monthly expenses currently exceed income streams. Avoid high card usage and non-essential transactions.")
            }
        }

        // 4. Goals performance
        if (goalsList.isNotEmpty()) {
            val target = goalsList.sumOf { it.targetAmount }
            val saved = goalsList.sumOf { it.savedAmount }
            val goalsRate = if (target == 0.0) 0.0 else (saved / target) * 100.0
            if (goalsRate > 0.0) {
                insightsList.add("🎯 Savings milestones: You have achieved ${String.format("%.0f", goalsRate)}% of your aggregate target portfolio values.")
            }
        }

        // 5. Categorical check
        val subscriptionsCount = transactionsList.count { it.type == "EXPENSE" && it.category.equals("Subscription", ignoreCase = true) }
        if (subscriptionsCount >= 2) {
            insightsList.add("☕ Active Subscriptions: You have $subscriptionsCount recurring charges this month. Standardize with yearly billing to save up to 15%.")
        }

        insightsList
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- MUTATION ACTORS ---

    fun insertTransaction(amount: Double, type: String, category: String, note: String, paymentMethod: String, date: Long) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    amount = amount,
                    type = type,
                    category = category,
                    note = note,
                    paymentMethod = paymentMethod,
                    date = date
                )
            )
        }
    }

    fun updateTransaction(id: Int, amount: Double, type: String, category: String, note: String, paymentMethod: String, date: Long) {
        viewModelScope.launch {
            repository.updateTransaction(
                Transaction(
                    id = id,
                    amount = amount,
                    type = type,
                    category = category,
                    note = note,
                    paymentMethod = paymentMethod,
                    date = date
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun insertGoal(title: String, target: Double, saved: Double, category: String, targetDate: Long) {
        viewModelScope.launch {
            repository.insertGoal(
                SavingsGoal(
                    title = title,
                    targetAmount = target,
                    savedAmount = saved,
                    category = category,
                    targetDate = targetDate
                )
            )
        }
    }

    fun updateGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun addSavingsToGoal(goal: SavingsGoal, amount: Double) {
        viewModelScope.launch {
            val updated = goal.copy(savedAmount = (goal.savedAmount + amount).coerceAtMost(goal.targetAmount))
            repository.updateGoal(updated)
            // Register an expense type transaction reflecting allocation to goal
            // Or simple local notation
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun editTransaction(transaction: Transaction?) {
        _editingTransaction.value = transaction
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setTypeFilter(type: String?) {
        _selectedTypeFilter.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateMonthlyBudget(newLimit: Double) {
        _monthlyBudget.value = newLimit
    }

    fun setMockMode(enabled: Boolean) {
        viewModelScope.launch {
            _isMockMode.value = enabled
            prefs.edit().putBoolean("is_mock_mode", enabled).apply()
            
            // Wipe standard operations
            repository.clearAllData()
            
            // Reload seeds if re-enabling
            if (enabled) {
                repository.seedDatabaseForce()
            }
        }
    }
}

class FlowTrackViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlowTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlowTrackViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
