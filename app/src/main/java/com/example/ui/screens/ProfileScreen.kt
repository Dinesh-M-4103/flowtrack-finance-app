package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.PurpleNeon
import com.example.viewmodel.FlowTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: FlowTrackViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val budgetLimit by viewModel.monthlyBudget.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val goals by viewModel.allGoals.collectAsStateWithLifecycle()

    var editingBudget by remember { mutableStateOf(false) }
    var budgetInputStr by remember { mutableStateOf(budgetLimit.toString()) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Preferences & Budget",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )
                        Text(
                            "Configure Limits, Theme & Cleanses",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Sleek User Avatar Display
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(PrimaryCyan, PrimaryNeon)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        "Fintech Investor",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "Member Level: Premium Platinum",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }

            // 2. Budget Settings Controller
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Budget Icon",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Monthly Outflow Budget",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(onClick = {
                            if (editingBudget) {
                                val value = budgetInputStr.toDoubleOrNull()
                                if (value != null && value > 0) {
                                    viewModel.updateMonthlyBudget(value)
                                    editingBudget = false
                                    Toast.makeText(context, "Budget limit synchronized!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Enter a valid budget limit", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                budgetInputStr = budgetLimit.toString()
                                editingBudget = true
                            }
                        }) {
                            Icon(
                                imageVector = if (editingBudget) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = "Edit budget",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (editingBudget) {
                        OutlinedTextField(
                            value = budgetInputStr,
                            onValueChange = { budgetInputStr = it },
                            label = { Text("Budget Cap Limit (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = "₹${String.format("%,.0f", budgetLimit)} / month",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = if (isDarkTheme) PrimaryNeon else Color(0xFF0284C7)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "A dynamic warnings engine triggers at 100% budget cap usage.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // 3. App Controls (Dark/Light Mode & Metrics)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "SYSTEM PARAMETERS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )

                    // Theme toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Theme Icon",
                                tint = PurpleNeon,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Cyber Dark Theme mode",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PrimaryNeon,
                                checkedTrackColor = PrimaryNeon.copy(alpha = 0.4f)
                            )
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Database Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Source,
                                contentDescription = "Data status",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Database Record Ledger",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${transactions.size} rows logged",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "${goals.size} portfolio milestones",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // 4. Data Engine Mode Switcher Card
            val isMockMode by viewModel.isMockMode.collectAsStateWithLifecycle()

            Text(
                "DATA FLOW ENGINE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeveloperMode,
                            contentDescription = "Data Engine Mode",
                            tint = if (isDarkTheme) PurpleNeon else Color(0xFF7C3AED),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Operation Paradigm",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Segmented button slider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isMockMode) (if (isDarkTheme) PurpleNeon else Color(0xFF7C3AED)) else Color.Transparent)
                                .clickable {
                                    viewModel.setMockMode(true)
                                    Toast.makeText(context, "Mock presentation mode reloaded!", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "MOCK MODE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isMockMode) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isMockMode) (if (isDarkTheme) PrimaryCyan else Color(0xFF0EA5E9)) else Color.Transparent)
                                .clickable {
                                    viewModel.setMockMode(false)
                                    Toast.makeText(context, "Real-time production ledger engaged!", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "REAL TIME",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (!isMockMode) (if (isDarkTheme) Color.Black else Color.White) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isMockMode) {
                            "Demonstration mode enabled. Preloaded with simulated high-fidelity transactions, recurring salary, and active savings portfolios to showcase dynamic graphs."
                        } else {
                            "Real-time empty state initialized. You hold total custody and control. Every ledger entry, income cycle, and progress milestone updates instantly from your inputs."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
