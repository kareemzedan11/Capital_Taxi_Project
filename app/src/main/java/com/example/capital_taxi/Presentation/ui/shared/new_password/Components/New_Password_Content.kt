package com.example.capital_taxi.Presentation.ui.shared.new_password.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R

@Composable
fun NewPasswordContent(
    password: String,
    onPasswordChange: (String) -> Unit,
    onSaveClicked: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            stringResource(R.string.create_new_password),
            fontWeight = FontWeight.W400,
            fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 24.sp,
                maxSize = 32.sp
            ),
            fontFamily = CustomFontFamily,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            stringResource(R.string.password_hint),
            fontWeight = FontWeight.W400,
            fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 14.sp,
                maxSize = 18.sp
            ),
            fontFamily = CustomFontFamily,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.new_password_label)) },
            placeholder = { Text(stringResource(R.string.new_password_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = .2f)),
            singleLine = true
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSaveClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.save_button), fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 14.sp,
                    maxSize = 18.sp
                ),
                fontFamily = CustomFontFamily, color = Color.Black
            )
        }
        Spacer(modifier = Modifier.weight(2f))
    }
}

