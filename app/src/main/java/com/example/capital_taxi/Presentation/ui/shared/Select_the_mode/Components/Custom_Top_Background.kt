package com.example.capital_taxi.Presentation.ui.shared.Select_the_mode.Components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun CustomTopBackground(generalColor: Color) {

    Box(
        modifier = Modifier

            .height(200.dp)
            .width(200.dp)
            .border(
                width = 2.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(bottomStart = 222.dp)
            )
            .drawBehind {
                // رسم اللون داخل الـ border فقط باستخدام اللون من resources
                val shape = RoundedCornerShape(bottomStart = 222.dp)
                val path = shape.createOutline(size, LayoutDirection.Ltr, this)

                drawOutline(outline = path, style = Fill, brush = SolidColor(generalColor))
            }
    )

}