package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Transaction
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PrimaryNeon
import com.example.viewmodel.FlowTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: FlowTrackViewModel,
    onNavigateBack: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val typeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()

    val categories = listOf("Food", "Fuel", "Shopping", "Bills", "Medical", "Travel", "Entertainment", "Subscription", "Salary", "Freelance", "Business")

    var showSearchRow by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    if (showSearchRow) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search comments or amounts...", fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.setSearchQuery("")
                                    showSearchRow = false
                                }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close search", modifier = Modifier.size(16.dp))
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    } else {
                        Column {
                            Text(
                                "Transaction Ledger",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                            )
                            Text(
                                "Live Search & Ledger Filters",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (!showSearchRow) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate Back"
                            )
                        }
                    }
                },
                actions = {
                    if (!showSearchRow) {
                        IconButton(onClick = { showSearchRow = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Open search",
                                tint = MaterialTheme.colorScheme.onBackground
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
        ) {
            // 1. Transaction Type Segment Filters (All / Outflow / Inflow)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (typeFilter == null) MaterialTheme.colorScheme.outline.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { viewModel.setTypeFilter(null) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "ALL LOGS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (typeFilter == "EXPENSE") MaterialTheme.colorScheme.outline.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { viewModel.setTypeFilter("EXPENSE") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "OUTFLOWS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (typeFilter == "INCOME") MaterialTheme.colorScheme.outline.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { viewModel.setTypeFilter("INCOME") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "INFLOWS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 2. Category Filter chips scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )

                // All Chip choice
                InputChip(
                    selected = categoryFilter == null,
                    onClick = { viewModel.setCategoryFilter(null) },
                    label = { Text("All", fontWeight = FontWeight.SemiBold) },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = PrimaryCyan,
                        selectedLabelColor = Color(0xFF0F172A)
                    )
                )

                categories.forEach { category ->
                    val isChosen = categoryFilter == category
                    InputChip(
                        selected = isChosen,
                        onClick = { viewModel.setCategoryFilter(if (isChosen) null else category) },
                        label = { Text(category, fontWeight = FontWeight.SemiBold) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = PrimaryCyan,
                            selectedLabelColor = Color(0xFF0F172A)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Transactions Display List with Empty Status illustration
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Empty",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No matching logs found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Relax filters, delete searches, or add new items to the Ledger",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionRow(
                            transaction = transaction,
                            onClick = { onEditTransaction(transaction) }
                        )
                    }
                }
            }
        }
    }
}
