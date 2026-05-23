package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class OnboardingSlide(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSlideIndex by remember { mutableStateOf(0) }

    val slides = listOf(
        OnboardingSlide(
            title = "Personal Ledger. Simplified.",
            description = "Track daily outflows, income cycles, and UPI balances in a beautiful, unified ledger. Fully offline and completely private.",
            icon = Icons.Default.ElectricBolt,
            accentColor = PrimaryNeon
        ),
        OnboardingSlide(
            title = "High-Fidelity Analytics",
            description = "Query your financial health instantly with interactive Canvas graphs. Analyze categories and ratio trends on demand.",
            icon = Icons.Default.QueryStats,
            accentColor = PrimaryCyan
        ),
        OnboardingSlide(
            title = "Achieve Future Targets",
            description = "Setup customizable savings goals. Fund milestones like bikes, laptops, or custom portfolios, with automated completion estimates.",
            icon = Icons.Default.TrackChanges,
            accentColor = PurpleNeon
        )
    )

    val currentSlide = slides[currentSlideIndex]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBg, Color(0xFF020304))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onOnboardingFinished) {
                    Text(
                        "SKIP",
                        color = TextSlate,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Slide Center Content with Crossfade animations
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedContent(
                    targetState = currentSlide,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "slide_transition"
                ) { slide ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Glossy animated icon bubble
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(CardDark)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                slide.accentColor.copy(alpha = 0.25f),
                                                Color.Transparent
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = slide.icon,
                                    contentDescription = slide.title,
                                    tint = slide.accentColor,
                                    modifier = Modifier.size(62.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            text = slide.title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = slide.description,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = TextSlate,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            // Lower Bottom navigation control
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    slides.forEachIndexed { idx, _ ->
                        val isSelected = idx == currentSlideIndex
                        val dWidth by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            label = "dot_width"
                        )
                        val dColor = if (isSelected) currentSlide.accentColor else CardDark

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(dWidth)
                                .background(dColor, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // High accent primary Button
                Button(
                    onClick = {
                        if (currentSlideIndex < slides.size - 1) {
                            currentSlideIndex++
                        } else {
                            onOnboardingFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentSlide.accentColor,
                        contentColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (currentSlideIndex == slides.size - 1) "ELEVATE FINANCE" else "CONTINUE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Chevron icon",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
