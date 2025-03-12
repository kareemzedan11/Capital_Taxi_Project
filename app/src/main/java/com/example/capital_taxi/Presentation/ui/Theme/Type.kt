package com.example.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import com.example.capital_taxi.R
import java.util.Locale

val CustomFontFamily: FontFamily
    get() {
        val currentLanguage = Locale.getDefault().language
        return if (currentLanguage == "ar") {
            FontFamily(
                Font(R.font.cairo_black) // Arabic font (Cairo)
            )
        } else {
            FontFamily(
                Font(R.font.montserrat_regular) // English font (Lato)
            )
        }
    }

@Composable
fun responsiveTextSize(
    fraction: Float,
    minSize: TextUnit = 12.sp,
    maxSize: TextUnit = 32.sp
): TextUnit {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val calculatedSize = (screenWidth * fraction).sp
    // Convert TextUnit to Float, apply coerceIn, and convert back to TextUnit
    val constrainedSize = calculatedSize.value.coerceIn(minSize.value, maxSize.value).sp
    return constrainedSize
}


// Typography styles
val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontSize = 30.sp // This can be made dynamic if needed
    ),
    titleMedium = TextStyle(
        fontFamily = CustomFontFamily,
        fontSize = 24.sp // Replace with responsiveTextSize if desired
    ),
    bodyLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontSize = 16.sp // Replace with responsiveTextSize if desired
    ),
    labelSmall = TextStyle(
        fontFamily = CustomFontFamily,
        fontSize = 14.sp // Replace with responsiveTextSize if desired
    )
)
