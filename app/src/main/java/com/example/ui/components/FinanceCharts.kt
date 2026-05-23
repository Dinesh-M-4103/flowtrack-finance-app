package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SavingsGoal
import com.example.data.Transaction
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Helper category color map
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food" -> NeonGreen
        "fuel" -> PrimaryCyan
        "shopping" -> PurpleNeon
        "bills" -> Color(0xFF3B82F6) // Electric Blue
        "medical" -> Color(0xFFF43F5E) // Salmon Red
        "travel" -> Color(0xFFF59E0B) // Amber
        "entertainment" -> Color(0xFFD946EF) // Magenta
        "subscription" -> Color(0xFF06B6D4) // Teal
        "salary" -> NeonGreen
        "freelance" -> PrimaryCyan
        "business" -> PurpleNeon
        "passive income", "passive" -> Color(0xFF10B981)
        else -> Color(0xFF64748B) // Slate Muted
    }
}

@Composable
fun CategoryDonutChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val expenses = transactions.filter { it.type == "EXPENSE" }
    val grouped = expenses.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } }
    val totalExpense = grouped.values.sum()

    // Animation progress
    var animatedProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(transactions) {
        animatedProgress = 0f
        animatedProgress = 1f
    }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "donut_progress"
    )

    if (totalExpense == 0.0) {
        Box(
            modifier = modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No spending data available to display",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    // Pie slices calculation
    val slices = grouped.map { (cat, amt) ->
        val sweepAngle = (amt / totalExpense * 360f).toFloat()
        val color = getCategoryColor(cat)
        DonutSlice(cat, amt, sweepAngle, color)
    }.sortedByDescending { it.amount }

    Row(
        modifier = modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Doughnut Canvas
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val strokeWidth = 32f

                slices.forEach { slice ->
                    val sweep = slice.sweepAngle * progress
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    )
                    startAngle += sweep
                }
            }

            // Center details
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TOTAL SPENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "₹${String.format("%,.0f", totalExpense)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legend List (Showing up to top 4 categories with bullet points)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            slices.take(4).forEach { slice ->
                val pet = (slice.amount / totalExpense) * 100
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(slice.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = slice.category,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1
                        )
                        Text(
                            text = "₹${String.format("%,.0f", slice.amount)} (${String.format("%.0f", pet)}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

data class DonutSlice(
    val category: String,
    val amount: Double,
    val sweepAngle: Float,
    val color: Color
)

@Composable
fun WeeklyLineChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val expenses = transactions.filter { it.type == "EXPENSE" }

    // Aggregate past 7 days spending
    val calendar = Calendar.getInstance()
    val dayAmountList = remember(transactions) {
        val days = mutableListOf<Pair<String, Float>>()
        val dayFormatter = SimpleDateFormat("E", Locale.getDefault())

        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = c.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val dayEnd = dayStart + (24 * 60 * 60 * 1000L) - 1

            val totalForDay = expenses.filter { it.date in dayStart..dayEnd }.sumOf { it.amount }.toFloat()
            days.add(Pair(dayFormatter.format(c.time), totalForDay))
        }
        days
    }

    val maxVal = remember(dayAmountList) {
        val max = dayAmountList.maxOfOrNull { it.second } ?: 0f
        if (max == 0f) 1000f else max * 1.15f
    }

    var animatedProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(transactions) {
        animatedProgress = 0f
        animatedProgress = 1f
    }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1100),
        label = "line_progress"
    )

    // Interaction states
    var selectedIndex by remember { mutableStateOf(-1) }
    var touchX by remember { mutableStateOf(0f) }

    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        // Selected Tooltip Display
        Row(
            modifier = Modifier.fillMaxWidth().height(26.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedIndex in dayAmountList.indices) {
                val data = dayAmountList[selectedIndex]
                Text(
                    text = "${data.first}: ₹${String.format("%,.0f", data.second)}",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNeon,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Tapped point",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "Weekly Spend Analysis",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Tap graph to query",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(dayAmountList) {
                        detectTapGestures(
                            onTap = { offset ->
                                val sectionWidth = size.width / 6f
                                val index = (offset.x / sectionWidth).toInt().coerceIn(0, 6)
                                selectedIndex = if (selectedIndex == index) -1 else index
                                touchX = offset.x
                            }
                        )
                    }
            ) {
                val width = size.width
                val height = size.height

                // 1. Draw horizontal grid lines & labels
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = (height / gridLines) * i
                    drawLine(
                        color = DarkBorder.copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 2f
                    )
                }

                // Points placement
                val points = mutableListOf<Offset>()
                val xSpacing = width / 6f

                for (i in 0..6) {
                    val amount = dayAmountList[i].second
                    val x = xSpacing * i
                    // scale from 0 to height (representing maxVal to 0)
                    val ratio = if (maxVal == 0f) 0f else (amount / maxVal)
                    val y = height - (height * ratio * progress)
                    points.add(Offset(x, y))
                }

                // 2. Draw standard spline curves
                val linePath = Path()
                if (points.isNotEmpty()) {
                    linePath.moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val controlX1 = prev.x + (curr.x - prev.x) / 2f
                        val controlY1 = prev.y
                        val controlX2 = prev.x + (curr.x - prev.x) / 2f
                        val controlY2 = curr.y

                        linePath.cubicTo(
                            controlX1, controlY1,
                            controlX2, controlY2,
                            curr.x, curr.y
                        )
                    }
                }

                // 3. Draw gradient bounds area under the curve
                val gradientPath = Path().apply {
                    addPath(linePath)
                    lineTo(points.last().x, height)
                    lineTo(points.first().x, height)
                    close()
                }

                drawPath(
                    path = gradientPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PrimaryNeon.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )

                // Draw Spline Line
                drawPath(
                    path = linePath,
                    color = PrimaryNeon,
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )

                // 4. Draw interactive dotted overlay indicator
                if (selectedIndex in 0..6) {
                    val selectedPoint = points[selectedIndex]
                    // draw vertical guideline
                    drawLine(
                        color = PrimaryNeon.copy(alpha = 0.6f),
                        start = Offset(selectedPoint.x, 0f),
                        end = Offset(selectedPoint.x, height),
                        strokeWidth = 3f
                    )
                    // draw halo circle and center node
                    drawCircle(
                        color = PrimaryNeon.copy(alpha = 0.3f),
                        radius = 24f,
                        center = selectedPoint
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 10f,
                        center = selectedPoint
                    )
                    drawCircle(
                        color = PrimaryNeon,
                        radius = 6f,
                        center = selectedPoint
                    )
                } else {
                    // Just draw tiny dots at points by default
                    points.forEach { point ->
                        drawCircle(
                            color = PrimaryCyan,
                            radius = 6f,
                            center = point
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // X Axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayAmountList.forEachIndexed { idx, item ->
                Text(
                    text = item.first,
                    modifier = Modifier.width(36.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = if (idx == selectedIndex) PrimaryNeon else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun MonthlyComparisonBarChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

    var animatedProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(transactions) {
        animatedProgress = 0f
        animatedProgress = 1f
    }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 800),
        label = "bar_progress"
    )

    val maxAmount = maxOf(totalIncome, totalExpense, 1000.0).toFloat()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = Stroke(width = 1f).let { null } // We will style card elegantly
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Cash Flow Ratio",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Bar 1: Income
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    val incHeightRatio = (totalIncome / maxAmount).toFloat()
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .fillMaxHeight(incHeightRatio * progress)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(PrimaryNeon, NeonGreen.copy(alpha = 0.5f))
                                ),
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Inflow",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Text(
                        "₹${String.format("%,.0f", totalIncome)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen
                    )
                }

                // Bar 2: Expense
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    val expHeightRatio = (totalExpense / maxAmount).toFloat()
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .fillMaxHeight(expHeightRatio * progress)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(NeonRed, Color(0xFFFF5252).copy(alpha = 0.5f))
                                ),
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Outflow",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Text(
                        "₹${String.format("%,.0f", totalExpense)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonRed
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsTrendChart(
    goals: List<SavingsGoal>,
    modifier: Modifier = Modifier
) {
    if (goals.isEmpty()) return

    val totalTarget = goals.sumOf { it.targetAmount }
    val totalSaved = goals.sumOf { it.savedAmount }

    var animatedProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(goals) {
        animatedProgress = 0f
        animatedProgress = 1f
    }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1200),
        label = "savings_progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Aggregated Target Progression",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "SAVINGS WALLET",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        "₹${String.format("%,.0f", totalSaved)} of ₹${String.format("%,.0f", totalTarget)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryCyan
                    )
                }

                val completionPercent = if (totalTarget == 0.0) 0f else (totalSaved / totalTarget).toFloat()
                Text(
                    "${String.format("%.1f", completionPercent * 100 * progress)}%",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = PrimaryNeon
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Progress bar track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                val completionPercent = if (totalTarget == 0.0) 0f else (totalSaved / totalTarget).toFloat()
                Box(
                    modifier = Modifier
                        .fillMaxWidth(completionPercent * progress)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryCyan, PrimaryNeon)
                            ),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
