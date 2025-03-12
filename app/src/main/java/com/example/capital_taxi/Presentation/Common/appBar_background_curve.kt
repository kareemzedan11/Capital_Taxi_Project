package com.example.capital_taxi.Presentation.Common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp


@Composable
fun appBar_background_curve(generalColor: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // تحديد ارتفاع ثابت للـ Canvas
    ) {
        val width = size.width
        val height = size.height

        // Define the curved path
        val path = Path().apply {
            moveTo(0f, height * 0.4f) // نقطة البداية
            cubicTo(
                width * 0.3f, height * 0.1f,
                width * 0.7f, height * 0.9f,
                width, height * 0.4f
            )
            lineTo(width, 0f)
            lineTo(0f, 0f)
            close()
        }

        // Draw a gradient inside the path
        clipPath(path) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        generalColor,
                        generalColor.copy(alpha = .4f),
                    ),
                    startY = 0f,
                    endY = height
                ),
                size = size
            )
        }
    }
}