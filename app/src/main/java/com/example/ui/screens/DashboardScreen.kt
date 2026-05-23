package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Transaction
import com.example.ui.components.WeeklyLineChart
import com.example.ui.components.getCategoryColor
import com.example.ui.theme.*
import com.example.viewmodel.FlowTrackViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FlowTrackViewModel,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val monthlyIncome by viewModel.monthlyIncome.collectAsStateWithLifecycle()
    val monthlyExpense by viewModel.monthlyExpense.collectAsStateWithLifecycle()
    val dailyAverageSpendingByViewModel by viewModel.dailyAverageSpending.collectAsStateWithLifecycle()
    val topCategoryPair by viewModel.topCategory.collectAsStateWithLifecycle()
    val insights by viewModel.financialInsights.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val displayTransactions = recentTransactions.take(4)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color(0xFF0F172A),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FLOWTRACK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = PrimaryNeon
                        )
                        Text(
                            text = "Dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAnalytics) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Analytics Hub",
                            tint = PrimaryCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Premium Master Card styled with metallic dark gradient & glowing corners
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF1A1C1E), Color(0xFF0F1012))
                            )
                        )
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(PrimaryNeon.copy(alpha = 0.08f), Color.Transparent),
                                center = Offset(x = 650f, y = -100f),
                                radius = 420f
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 26.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "CURRENT LIQUIDITY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = "Active Status Icon",
                                tint = PrimaryNeon,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large formatted integral currency representation
                        Text(
                            text = "₹${String.format("%,.2f", totalBalance)}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-1.5).sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        // Inflow / Outflow ratios on card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "MONTHLY INFLOW",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = "Inflow Arrow",
                                        tint = PrimaryNeon,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "₹${String.format("%,.0f", monthlyIncome)}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "MONTHLY OUTFLOW",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingDown,
                                        contentDescription = "Outflow Arrow",
                                        tint = NeonRed,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "₹${String.format("%,.0f", monthlyExpense)}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Elegant Dual-Column Stats Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Outflow Spending / Daily Avg box
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(98.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x0AFFFFFF)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "SPENDING AVG",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${String.format("%,.0f", dailyAverageSpendingByViewModel)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.White
                                )
                            }
                            // Custom fine line progress indicator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.72f)
                                        .fillMaxHeight()
                                        .background(PrimaryCyan, CircleShape)
                                )
                            }
                        }
                    }

                    // Top Category / Income box
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(98.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x0AFFFFFF)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "TOP CATEGORY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = topCategoryPair.first.uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = getCategoryColor(topCategoryPair.first),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            // Custom fine line progress indicator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .fillMaxHeight()
                                        .background(PrimaryNeon, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Dynamic Calculated Insights Drawer
            item {
                if (insights.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "SMART FINANCIAL INSIGHTS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Rotate or list top 2 insights neatly
                        insights.take(2).forEach { insight ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (insight.contains("⚠️") || insight.contains("warning")) Icons.Default.Warning else Icons.Default.CheckCircle,
                                        contentDescription = "Insight Tag",
                                        tint = if (insight.contains("⚠️")) NeonRed else PrimaryNeon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = insight.substringAfter(" "),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Quick Actions / Navigation Roll
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionButton(
                        title = "Ledger",
                        icon = Icons.Default.FormatListBulleted,
                        color = PrimaryNeon,
                        onClick = onNavigateToTransactions
                    )
                    QuickActionButton(
                        title = "Analytics",
                        icon = Icons.Default.Analytics,
                        color = PrimaryCyan,
                        onClick = onNavigateToAnalytics
                    )
                    QuickActionButton(
                        title = "Goals",
                        icon = Icons.Default.TrackChanges,
                        color = PurpleNeon,
                        onClick = onNavigateToGoals
                    )
                }
            }

            // 5. Embedded Weekly Spend Spline Chart
            if (recentTransactions.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Weekly Spending Spline",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                IconButton(onClick = onNavigateToAnalytics) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Full Analytics",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            WeeklyLineChart(transactions = recentTransactions)
                        }
                    }
                }
            }

            // 6. Recent Transaction Ledger Feed Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT TRANSACTIONS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    TextButton(onClick = onNavigateToTransactions) {
                        Text(
                            text = "SEE ALL",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // 7. Recent Items
            if (displayTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCard,
                                contentDescription = "Ready to start",
                                tint = if (isDarkTheme) PrimaryCyan else Color(0xFF0EA5E9),
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Start your ledger journey",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "Your offline cash flows and credit limits are empty. Register your first transaction to animate the Canvas intelligence charts.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onNavigateToAddTransaction,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDarkTheme) PrimaryCyan else Color(0xFF0EA5E9),
                                        contentColor = if (isDarkTheme) Color.Black else Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.5f)
                                ) {
                                    Text("ADD LEDGER ENTRY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(context, "Tip: Enable presentation dummy data under limits tab to explore fully!", Toast.LENGTH_LONG).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("DEMO TIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                items(displayTransactions) { item ->
                    TransactionRow(
                        transaction = item,
                        onClick = { onEditTransaction(item) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = formatter.format(Date(transaction.date))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category Colored Icon Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(getCategoryColor(transaction.category).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = getCategoryIconVector(transaction.category)
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.category,
                        tint = getCategoryColor(transaction.category),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.note.ifEmpty { transaction.category },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$formattedDate • ${transaction.paymentMethod}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            // Prefix Amount Inflow vs Outflow check
            val isExpense = transaction.type == "EXPENSE"
            val sign = if (isExpense) "-" else "+"
            val color = if (isExpense) NeonRed else NeonGreen
            Text(
                text = "$sign ₹${String.format("%,.0f", transaction.amount)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = color
            )
        }
    }
}

// Map strings cleanly to corresponding visual Material symbols
fun getCategoryIconVector(category: String): ImageVector {
    return when (category.lowercase()) {
        "food" -> Icons.Default.Fastfood
        "fuel" -> Icons.Default.LocalGasStation
        "shopping" -> Icons.Default.ShoppingBag
        "bills" -> Icons.Default.ReceiptLong
        "medical" -> Icons.Default.MedicalServices
        "travel" -> Icons.Default.Flight
        "entertainment" -> Icons.Default.LiveTv
        "subscription" -> Icons.Default.Subscriptions
        "salary", "business", "freelance", "passive", "passive income" -> Icons.Default.MonetizationOn
        else -> Icons.Default.Category
    }
}
