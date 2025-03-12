package com.example.capital_taxi.Presentation.ui.Passengar.Screens.InviteFriendsPage.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R

@Composable
fun CodeBox(code: String) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(colorResource(R.color.primary_color), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .padding(horizontal = 5.dp)
        ) {
            Text(
                text = stringResource(R.string.referral_code),
                fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 12.sp,
                    maxSize = 16.sp
                ),
                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                BasicText(
                    text = code,
                    style = TextStyle(   fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 16.sp,
                        maxSize = 20.sp
                    ),
                        fontFamily = CustomFontFamily, fontWeight = FontWeight.Medium)
                )
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { copyToClipboard(context, code) },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color))
                ) {
                    Text(stringResource(R.string.copy_code), color = Color.Black)
                }
            }
        }
    }
}
