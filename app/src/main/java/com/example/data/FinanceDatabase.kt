package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.Calendar

@Database(entities = [Transaction::class, SavingsGoal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun savingsGoalDao(): SavingsGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flowtrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class FinanceRepository(private val db: AppDatabase) {
    private val transactionDao = db.transactionDao()
    private val savingsGoalDao = db.savingsGoalDao()

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val allGoals: Flow<List<SavingsGoal>> = savingsGoalDao.getAllGoals()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun insertGoal(goal: SavingsGoal) {
        savingsGoalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: SavingsGoal) {
        savingsGoalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: SavingsGoal) {
        savingsGoalDao.deleteGoal(goal)
    }

    suspend fun deleteGoalById(id: Int) {
        savingsGoalDao.deleteGoalById(id)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        db.clearAllTables()
    }

    suspend fun seedDatabaseForce() = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        val oneDayMs = 24 * 60 * 60 * 1000L

        // 1. Seed Savings Goals
        val mockGoals = listOf(
            SavingsGoal(title = "Next Gen MacBook Pro", targetAmount = 180000.0, savedAmount = 110000.0, category = "Laptop", targetDate = now + 45 * oneDayMs),
            SavingsGoal(title = "6-Month Emergency Fund", targetAmount = 200000.0, savedAmount = 145000.0, category = "Emergency Fund", targetDate = now + 120 * oneDayMs),
            SavingsGoal(title = "Superbike Kawasaki", targetAmount = 450000.0, savedAmount = 150000.0, category = "Bike", targetDate = now + 180 * oneDayMs),
            SavingsGoal(title = "Euro Summer Vacation", targetAmount = 300000.0, savedAmount = 90000.0, category = "Vacation", targetDate = now + 90 * oneDayMs)
        )
        for (g in mockGoals) {
            savingsGoalDao.insertGoal(g)
        }

        // 2. Seed Transactions (spread over past 10 days)
        val mockTransactions = listOf(
            // Incomes
            Transaction(amount = 95000.0, type = "INCOME", category = "Salary", note = "Monthly Core Freelance & Salary Pay", date = now - 9 * oneDayMs, paymentMethod = "Bank Transfer"),
            Transaction(amount = 18500.0, type = "INCOME", category = "Freelance", note = "Mobile App UI Project Delivery", date = now - 4 * oneDayMs, paymentMethod = "UPI"),
            Transaction(amount = 5400.0, type = "INCOME", category = "Passive income", note = "Stock Dividends payout", date = now - 1 * oneDayMs, paymentMethod = "UPI"),
            Transaction(amount = 8000.0, type = "INCOME", category = "Business", note = "E-Commerce Store Revenue", date = now - 6 * oneDayMs, paymentMethod = "UPI"),

            // Expenses
            Transaction(amount = 16000.0, type = "EXPENSE", category = "Bills", note = "Co-Working Desk & Rent", date = now - 8 * oneDayMs, paymentMethod = "Bank Transfer"),
            Transaction(amount = 3200.0, type = "EXPENSE", category = "Bills", note = "High-speed Fiber Broadband & AWS", date = now - 7 * oneDayMs, paymentMethod = "Card"),
            Transaction(amount = 450.0, type = "EXPENSE", category = "Food", note = "Starbucks Cold Brew with Waffles", date = now - 6 * oneDayMs, paymentMethod = "UPI"),
            Transaction(amount = 4200.0, type = "EXPENSE", category = "Shopping", note = "Zara Linen Shirts & Pants", date = now - 5 * oneDayMs, paymentMethod = "Card"),
            Transaction(amount = 1100.0, type = "EXPENSE", category = "Fuel", note = "Shell Fuel Premium Fillup", date = now - 5 * oneDayMs, paymentMethod = "UPI"),
            Transaction(amount = 3900.0, type = "EXPENSE", category = "Medical", note = "Dental scaling and routine checkup", date = now - 4 * oneDayMs, paymentMethod = "Card"),
            Transaction(amount = 1500.0, type = "EXPENSE", category = "Food", note = "Gourmet Wood-Fired Pizza dinner", date = now - 3 * oneDayMs, paymentMethod = "UPI"),
            Transaction(amount = 649.0, type = "EXPENSE", category = "Subscription", note = "Netflix Premium UHD Plan", date = now - 3 * oneDayMs, paymentMethod = "Card"),
            Transaction(amount = 899.0, type = "EXPENSE", category = "Subscription", note = "Spotify Family Duo Premium", date = now - 2 * oneDayMs, paymentMethod = "Card"),
            Transaction(amount = 1450.0, type = "EXPENSE", category = "Travel", note = "Uber Airport Ride Taxi", date = now - 2 * oneDayMs, paymentMethod = "UPI"),
            Transaction(amount = 2200.0, type = "EXPENSE", category = "Entertainment", note = "IMAX Interstellar Movie Tickets", date = now - 1 * oneDayMs, paymentMethod = "UPI"),
            Transaction(amount = 480.0, type = "EXPENSE", category = "Others", note = "Organic Coffee Beans pack", date = now, paymentMethod = "UPI")
        )
        for (t in mockTransactions) {
            transactionDao.insertTransaction(t)
        }
    }

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val transactions = allTransactions.firstOrNull() ?: emptyList()
        val goals = allGoals.firstOrNull() ?: emptyList()

        if (transactions.isEmpty() && goals.isEmpty()) {
            seedDatabaseForce()
        }
    }
}
