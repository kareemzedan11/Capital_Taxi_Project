package com.example.capital_taxi.Presentation.ui.shared.Select_the_mode.Components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath


@Composable
fun CustomBottomBackground(generalColor: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height * 0.6f)
            cubicTo(
                width * 0.3f, height * 0.8f,
                width * 1f, height * 1f,
                width, height * 1f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        clipPath(path) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        generalColor,
                        generalColor,
                    ),
                    startY = 0f,
                    endY = height
                ),
                size = size
            )
        }
    }
}
