package com.example.capital_taxi.Presentation.Common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R


@Composable
fun TermsAndConditionsCheckbox(isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(colorResource(R.color.primary_color))
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.terms_conditions),
                fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 12.sp,
                    maxSize = 16.sp
                ),
                fontFamily = CustomFontFamily,
                modifier = Modifier.clickable {}
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(
                    id = R.string.terms_conditions_accept
                ),
                fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 12.sp,
                    maxSize = 16.sp
                ),
                fontFamily = CustomFontFamily,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {},
                color = Color.Red
            )
        }
    }
}
