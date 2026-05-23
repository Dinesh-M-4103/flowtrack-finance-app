package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CategoryDonutChart
import com.example.ui.components.MonthlyComparisonBarChart
import com.example.ui.components.SavingsTrendChart
import com.example.ui.components.WeeklyLineChart
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PrimaryNeon
import com.example.viewmodel.FlowTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: FlowTrackViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val allGoals by viewModel.allGoals.collectAsStateWithLifecycle()
    val insights by viewModel.financialInsights.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Finance Intelligence",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )
                        Text(
                            "Canvas Visuals & Ratio Analytics",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        if (allTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "Empty Metrics",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Analytics Hub Silenced",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Register transactions manually in the Home tab, or activate Mock Presentation Mode inside the limits panel to fill this intelligence canvas with analytics ratios.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Weekly Spending Spline Spline
                item {
                    Text(
                        "OUTFLOW WEEKLY TRENDS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            WeeklyLineChart(transactions = allTransactions)
                        }
                    }
                }

                // 2. Spending Category Donut
                item {
                    Text(
                        "CONSOLIDATED SPEND RATIOS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Expenditure Spread",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CategoryDonutChart(transactions = allTransactions)
                        }
                    }
                }

                // 3. CashFlow dynamic bars
                item {
                    Text(
                        "LIQUIDITY RATIO",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MonthlyComparisonBarChart(transactions = allTransactions)
                }

                // 4. Savings Targets aggregate progression
                if (allGoals.isNotEmpty()) {
                    item {
                        Text(
                            "ASSET GOALS ACCUMULATION",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SavingsTrendChart(goals = allGoals)
                    }
                }

                // 5. Full Insights bullet lists
                if (insights.isNotEmpty()) {
                    item {
                        Text(
                            "LEDGER INTELLIGENCE REPORTS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(32.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                              ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TipsAndUpdates,
                                        contentDescription = "Sleek Intelligence Banner",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Computed Health Insights",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                                insights.forEach { insight ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Point",
                                            tint = PrimaryCyan,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = insight.substringAfter(" "),
                                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}
