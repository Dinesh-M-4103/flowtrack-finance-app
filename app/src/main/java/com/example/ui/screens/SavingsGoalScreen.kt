package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SavingsGoal
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.PurpleNeon
import com.example.viewmodel.FlowTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalScreen(
    viewModel: FlowTrackViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val goals by viewModel.allGoals.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var fundingGoal by remember { mutableStateOf<SavingsGoal?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = PurpleNeon,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New goal",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Savings Portfolio",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )
                        Text(
                            "Fund Target Milestone Portfolios",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (goals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = "Empty portfolio",
                            tint = PurpleNeon.copy(alpha = 0.4f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No active milestones created",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Create targets like laptops, bikes or emergency reserves",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)
                        ) {
                            Text("INITIALIZE PROTOCOL", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(goals) { goal ->
                        SavingsGoalItemCard(
                            goal = goal,
                            onFund = { fundingGoal = goal },
                            onDelete = {
                                viewModel.deleteGoal(goal)
                                Toast.makeText(context, "Milestone removed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // CREATE MILESTONE MODAL
    if (showCreateDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var goalTarget by remember { mutableStateOf("") }
        var goalInitialState by remember { mutableStateOf("") }
        var goalCategory by remember { mutableStateOf("Laptop") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Initialize Asset Goal",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Goal Title / Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = goalTarget,
                        onValueChange = { goalTarget = it },
                        label = { Text("Target Finance Cost (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = goalInitialState,
                        onValueChange = { goalInitialState = it },
                        label = { Text("Pre-funded Balance (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    // Simple category drop mapping
                    Text("Milestone Type Category", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Laptop", "Bike", "Emergency Fund", "Vacation").forEach { type ->
                            val isChosen = goalCategory == type
                            val color = if (isChosen) PurpleNeon else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color)
                                    .clickable { goalCategory = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    type,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChosen) Color.White else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = goalTarget.toDoubleOrNull()
                        val initial = goalInitialState.toDoubleOrNull() ?: 0.0
                        if (goalTitle.isEmpty()) {
                            Toast.makeText(context, "Please set a goal name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (target == null || target <= 0) {
                            Toast.makeText(context, "Please configure valid targets", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.insertGoal(
                            title = goalTitle,
                            target = target,
                            saved = initial,
                            category = goalCategory,
                            targetDate = System.currentTimeMillis() + 90L * 24 * 3600 * 1000 // default 90 days
                        )
                        showCreateDialog = false
                        Toast.makeText(context, "Milestone initialized", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)
                ) {
                    Text("CREATE TARGET", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // FUND TRANSFER TRANSFER DIALOG
    if (fundingGoal != null) {
        var investValue by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { fundingGoal = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Fund: ${fundingGoal?.title}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Column {
                    Text("Input savings cash to transfer from checkouts to this milestone target:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = investValue,
                        onValueChange = { investValue = it },
                        label = { Text("Allocation Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val funds = investValue.toDoubleOrNull()
                        if (funds == null || funds <= 0.0) {
                            Toast.makeText(context, "Enter a valid funding amount", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addSavingsToGoal(fundingGoal!!, funds)
                        fundingGoal = null
                        Toast.makeText(context, "Portfolio funded", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon, contentColor = Color(0xFF0F172A))
                ) {
                    Text("ALLOCATE CASH")
                }
            },
            dismissButton = {
                TextButton(onClick = { fundingGoal = null }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun SavingsGoalItemCard(
    goal: SavingsGoal,
    onFund: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)

    val icon = when (goal.category.lowercase()) {
        "laptop" -> Icons.Default.Computer
        "bike" -> Icons.Default.TwoWheeler
        "emergency fund" -> Icons.Default.AccountBalanceWallet
        "vacation" -> Icons.Default.FlightTakeoff
        else -> Icons.Default.Flag
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color(0x0DFFFFFF))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(PurpleNeon.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = goal.category,
                            tint = PurpleNeon,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = goal.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }

                // Delete Milestone Button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Funding Statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "SAVED / TARGET",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        "₹${String.format("%,.0f", goal.savedAmount)} / ₹${String.format("%,.0f", goal.targetAmount)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "COMPLETION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        "${String.format("%.1f", progress * 100)}%",
                        fontWeight = FontWeight.Bold,
                        color = if (progress >= 1f) PrimaryNeon else PrimaryCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Linear progress indicator track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PurpleNeon, PrimaryCyan)
                            ),
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (progress >= 1f) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Completed",
                            tint = PrimaryNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Target Achieved!",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryNeon
                        )
                    }
                } else {
                    Text(
                        text = "₹${String.format("%,.0f", remaining)} outstanding",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                Button(
                    onClick = onFund,
                    enabled = progress < 1f,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleNeon,
                        disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("FUND MILESTONE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
