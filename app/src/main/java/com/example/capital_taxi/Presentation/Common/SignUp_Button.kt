package com.example.capital_taxi.Presentation.Common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R


@Composable
fun SignUpButton(isEnabled: Boolean, onClick: () -> Unit,text:Int) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color))
    ) {
        Text(
            text = stringResource(id =text),
            fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 14.sp,
                maxSize = 18.sp
            ),
            fontFamily = CustomFontFamily,
            color = Color.Black
        )
    }
}

