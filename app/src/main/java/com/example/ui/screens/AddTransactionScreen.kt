package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.theme.NeonRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PrimaryNeon
import com.example.viewmodel.FlowTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: FlowTrackViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val editingTx by viewModel.editingTransaction.collectAsState()

    val isEditing = editingTx != null

    // Input States
    var amountStr by remember { mutableStateOf(if (isEditing) editingTx!!.amount.toString() else "") }
    var selectedType by remember { mutableStateOf(if (isEditing) editingTx!!.type else "EXPENSE") }
    var selectedCategory by remember { mutableStateOf(if (isEditing) editingTx!!.category else "Food") }
    var noteStr by remember { mutableStateOf(if (isEditing) editingTx!!.note else "") }
    var selectedPaymentMethod by remember { mutableStateOf(if (isEditing) editingTx!!.paymentMethod else "UPI") }

    // Categories mapping
    val expenseCategories = listOf("Food", "Fuel", "Shopping", "Bills", "Medical", "Travel", "Entertainment", "Subscription", "Others")
    val incomeCategories = listOf("Salary", "Freelance", "Business", "Passive", "Others")

    // Reset standard category choice if type shifts
    LaunchedEffect(selectedType) {
        if (!isEditing) {
            selectedCategory = if (selectedType == "EXPENSE") "Food" else "Salary"
        }
    }

    val finalCategories = if (selectedType == "EXPENSE") expenseCategories else incomeCategories

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Modify Ledger" else "New Logging",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.editTransaction(null) // clear edit state
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancel"
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            editingTx?.let {
                                viewModel.deleteTransaction(it)
                                viewModel.editTransaction(null)
                                Toast.makeText(context, "Transaction deleted", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete entry",
                                tint = NeonRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Amount Display Bold input
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ENTER AMOUNT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.toDoubleOrNull() != null || input.all { it.isDigit() || it == '.' }) {
                            amountStr = input
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    placeholder = {
                        Text(
                            "₹0.00",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent
                    )
                )
            }

            // 2. Type Selector (Pills)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedType == "EXPENSE") NeonRed else Color.Transparent)
                        .clickable { selectedType = "EXPENSE" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "OUTFLOW (EXPENSE)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selectedType == "EXPENSE") Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedType == "INCOME") PrimaryNeon else Color.Transparent)
                        .clickable { selectedType = "INCOME" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "INFLOW (INCOME)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selectedType == "INCOME") Color(0xFF0F172A) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // 3. Category Select label and scroll chips
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "CHOOSE TRANSACTION CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    finalCategories.forEach { category ->
                        val isSelected = selectedCategory == category
                        val chipBg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        val chipText = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onBackground

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color(0xFF080D1A),
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }

            // 4. Note Input Text Box
            Column {
                Text(
                    "DESCRIPTION / COMMENT NOTE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteStr,
                    onValueChange = { noteStr = it },
                    placeholder = { Text("Starbucks with friends...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // 5. Payment method chips
            Column {
                Text(
                    "PAYMENT CHANNEL / METHOD",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("UPI", "Card", "Cash", "Bank").forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        val tintColor = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surface
                        val inkColor = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onBackground

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(tintColor)
                                .clickable { selectedPaymentMethod = method },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = method,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = inkColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 6. Primary Save action button
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (isEditing) {
                        viewModel.updateTransaction(
                            id = editingTx!!.id,
                            amount = amount,
                            type = selectedType,
                            category = selectedCategory,
                            note = noteStr,
                            paymentMethod = selectedPaymentMethod,
                            date = editingTx!!.date // preserve date
                        )
                        viewModel.editTransaction(null)
                        Toast.makeText(context, "Ledger updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.insertTransaction(
                            amount = amount,
                            type = selectedType,
                            category = selectedCategory,
                            note = noteStr,
                            paymentMethod = selectedPaymentMethod,
                            date = System.currentTimeMillis() // log timestamp
                        )
                        Toast.makeText(context, "Transaction logged", Toast.LENGTH_SHORT).show()
                    }
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color(0xFF0F172A)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Save Action Icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditing) "SAVE ledger CHANGES" else "LOG TRANSACTION",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
