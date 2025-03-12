package com.example.capital_taxi.Presentation.ui.Passengar.Screens.payment.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.payment.PaymentMethod


@Composable
fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onSelect() }
            .background(
                color = if (isSelected) Color(0xFFFFF8E1) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Payment Method Icon
        Icon(
            painter = painterResource(id = paymentMethod.iconRes),
            contentDescription = paymentMethod.name,
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)

                .background(Color.Yellow),

            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Payment Method Details
        Column(modifier = Modifier.weight(1f)) {
            Text(paymentMethod.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(paymentMethod.description, color = Color.Gray, fontSize = 14.sp)
            Text(paymentMethod.discount, color = Color.Gray, fontSize = 14.sp)
        }

        // Selection Indicator
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.Yellow else Color.LightGray)
        )
    }
}
