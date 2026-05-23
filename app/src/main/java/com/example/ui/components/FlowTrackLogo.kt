package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PurpleNeon

@Composable
fun FlowTrackLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    tintColor: Color? = null
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            val brush = if (tintColor != null) {
                Brush.linearGradient(colors = listOf(tintColor, tintColor))
            } else {
                Brush.linearGradient(
                    colors = listOf(PrimaryCyan, PurpleNeon)
                )
            }

            // Draw a futuristic double chevron representing financial flow
            val path1 = Path().apply {
                moveTo(w * 0.25f, h * 0.35f)
                lineTo(w * 0.5f, h * 0.60f)
                lineTo(w * 0.75f, h * 0.35f)
            }
            drawPath(
                path = path1,
                brush = brush,
                style = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
            )

            val path2 = Path().apply {
                moveTo(w * 0.25f, h * 0.58f)
                lineTo(w * 0.5f, h * 0.83f)
                lineTo(w * 0.75f, h * 0.58f)
            }
            drawPath(
                path = path2,
                brush = brush,
                style = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
            )

            // Glowing structural dot at the crown center
            drawCircle(
                brush = brush,
                radius = w * 0.08f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.16f)
            )
        }
    }
}
