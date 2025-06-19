package com.example.capital_taxi.Presentation.ui.shared.new_password.Components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R

@Composable
fun PasswordStrengthIndicator(password: String, modifier: Modifier = Modifier) {
    val strength = calculatePasswordStrength(password)

    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = { strength.strength },  // Note the lambda here for Material 3
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            trackColor = Color.LightGray,
            color = strength.color
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(strength.message),
            style = MaterialTheme.typography.labelSmall,
            color = strength.color
        )
    }
}

private fun calculatePasswordStrength(password: String): PasswordStrength {
    return when {
        password.isEmpty() -> PasswordStrength(0f, R.string.password_strength_none, Color.Gray)
        password.length < 6 -> PasswordStrength(0.3f, R.string.password_strength_weak, Color.Red)
        password.length < 8 -> PasswordStrength(0.6f, R.string.password_strength_medium, Color.Yellow)
        password.any { it.isDigit() } && password.any { it.isLetter() } &&
                password.any { !it.isLetterOrDigit() } ->
            PasswordStrength(1f, R.string.password_strength_strong, Color.Green)
        else -> PasswordStrength(0.8f, R.string.password_strength_good, Color(0xFF4CAF50))
    }
}

data class PasswordStrength(
    val strength: Float,  // Value between 0 and 1
    val message: Int,     // String resource ID
    val color: Color
)