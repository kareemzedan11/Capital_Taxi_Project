package com.example.capital_taxi.Presentation.ui.shared.Onboarding.Components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize


@Composable
  fun googlePhoneSignInButton(
    onSignInClick: () -> Unit,
    navController: NavController,
    text: String,
    image: Int,
    color: Boolean? = false
) {
    TextButton(
        onClick = { onSignInClick() },
        modifier = Modifier
            .fillMaxWidth(0.57f)
            .height(60.dp)
            .border(
                2.dp,
                color = Color(0XFFf2edde),
                RoundedCornerShape(10.dp)
            ),
        colors = ButtonDefaults.buttonColors(if (color ?: true) Color(0XFFf2edde) else Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                tint = Color(0XFF987200),
                contentDescription = null,
                painter = painterResource(image)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text,
                fontWeight = FontWeight.W500,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 16.sp),
                color = Color(0XFF987200),

                fontFamily = CustomFontFamily
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
