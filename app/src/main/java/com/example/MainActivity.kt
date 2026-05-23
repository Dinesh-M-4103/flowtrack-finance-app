package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.data.Transaction
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.FlowTrackViewModel
import com.example.viewmodel.FlowTrackViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Global App Theme State (Dark Theme First as requested!)
            var isDarkTheme by remember { mutableStateOf(true) }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val viewModel: FlowTrackViewModel by viewModels {
                    FlowTrackViewModelFactory(application)
                }

                AppOrchestrator(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

enum class AppWorkflowState {
    SPLASH, ONBOARDING, MAIN
}

enum class MainTab {
    DASHBOARD, LEDGER, ANALYTICS, SAVINGS, SETTINGS
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppOrchestrator(
    viewModel: FlowTrackViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var workflowState by remember { mutableStateOf(AppWorkflowState.SPLASH) }
    var activeTab by remember { mutableStateOf(MainTab.DASHBOARD) }

    // Navigation state overlays
    var isAddingTransaction by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = workflowState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "workflow_navigation"
        ) { state ->
            when (state) {
                AppWorkflowState.SPLASH -> {
                    SplashScreen(
                        onSplashFinished = {
                            workflowState = AppWorkflowState.ONBOARDING
                        }
                    )
                }

                AppWorkflowState.ONBOARDING -> {
                    OnboardingScreen(
                        onOnboardingFinished = {
                            workflowState = AppWorkflowState.MAIN
                        }
                    )
                }

                AppWorkflowState.MAIN -> {
                    MainScaffoldLayout(
                        viewModel = viewModel,
                        activeTab = activeTab,
                        onTabSelected = { activeTab = it },
                        onNavigateToAdd = { isAddingTransaction = true },
                        onEditTransaction = { transaction ->
                            viewModel.editTransaction(transaction)
                            isAddingTransaction = true
                        },
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme
                    )
                }
            }
        }

        // Add / Edit transaction sheet overlay with slide-up transition animation
        AnimatedVisibility(
            visible = isAddingTransaction,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 350)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut()
        ) {
            AddTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    isAddingTransaction = false
                }
            )
        }
    }
}

@Composable
fun MainScaffoldLayout(
    viewModel: FlowTrackViewModel,
    activeTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    onNavigateToAdd: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Modern styled bottom navigation bar
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val tabsSpecs = listOf(
                    NavigationTabSpec(MainTab.DASHBOARD, "Home", Icons.Default.Dashboard),
                    NavigationTabSpec(MainTab.LEDGER, "Ledger", Icons.Default.FormatListBulleted),
                    NavigationTabSpec(MainTab.ANALYTICS, "Hub", Icons.Default.Analytics),
                    NavigationTabSpec(MainTab.SAVINGS, "Goals", Icons.Default.TrackChanges),
                    NavigationTabSpec(MainTab.SETTINGS, "Limits", Icons.Default.Settings)
                )

                tabsSpecs.forEach { spec ->
                    val isSelected = activeTab == spec.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onTabSelected(spec.tab) },
                        label = { Text(spec.title) },
                        icon = {
                            Icon(
                                imageVector = spec.icon,
                                contentDescription = spec.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab contents under crossfades
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tab_content_transition"
            ) { tab ->
                when (tab) {
                    MainTab.DASHBOARD -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToAddTransaction = onNavigateToAdd,
                            onNavigateToTransactions = { onTabSelected(MainTab.LEDGER) },
                            onNavigateToAnalytics = { onTabSelected(MainTab.ANALYTICS) },
                            onNavigateToGoals = { onTabSelected(MainTab.SAVINGS) },
                            onEditTransaction = onEditTransaction,
                            isDarkTheme = isDarkTheme
                        )
                    }

                    MainTab.LEDGER -> {
                        TransactionsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { onTabSelected(MainTab.DASHBOARD) },
                            onEditTransaction = onEditTransaction
                        )
                    }

                    MainTab.ANALYTICS -> {
                        AnalyticsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { onTabSelected(MainTab.DASHBOARD) }
                        )
                    }

                    MainTab.SAVINGS -> {
                        SavingsGoalScreen(
                            viewModel = viewModel,
                            onNavigateBack = { onTabSelected(MainTab.DASHBOARD) }
                        )
                    }

                    MainTab.SETTINGS -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme
                        )
                    }
                }
            }
        }
    }
}

data class NavigationTabSpec(
    val tab: MainTab,
    val title: String,
    val icon: ImageVector
)
