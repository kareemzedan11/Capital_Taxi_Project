package com.example.capital_taxi.Presentation.ui.Passengar.Screens.settings.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R


@Composable
fun AppearanceBottomSheetContent(cancelClick:()->Unit,isDark: Boolean, onSave: (Boolean) -> Unit, ) {
    var selectedMode by remember { mutableStateOf(isDark) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.Appearance),
            fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 18.sp,
                maxSize = 24.sp
            ),
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppearanceOption(
            title = stringResource(id = R.string.light_mode),
            icon = painterResource(R.drawable.baseline_light_mode_24),
            isSelected = !selectedMode
        ) { selectedMode = false }
        Spacer(modifier = Modifier.height(16.dp))
        AppearanceOption(
            title = stringResource(id = R.string.dark_mode),
            icon = painterResource(R.drawable.baseline_dark_mode_24),
            isSelected = selectedMode
        ) { selectedMode = true }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSave(selectedMode) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.save_button),
                fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 14.sp,
                    maxSize = 18.sp
                ),
                fontFamily = CustomFontFamily,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.clickable {cancelClick()  },
            text = stringResource(id = R.string.cancel_button),
            fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 16.sp,
                maxSize = 20.sp
            ),
            fontFamily = CustomFontFamily,
            color = Color.Black,
            fontWeight = FontWeight.W700
        )

    }
}
