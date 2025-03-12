package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R


@Composable
fun OptionButtonWithMenu(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String,
    onMenuClick: () -> Unit,
    showIcon: Boolean
) {

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 12.dp)
                .background( colorResource(R.color.secondary_color),),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            if (showIcon && icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorResource(R.color.primary_color),
                    modifier = Modifier.size(24.dp)
                )

            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(20.dp))


            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown, // Menu icon
                    contentDescription = "KeyboardArrowDown",
                    tint = Color.Gray
                )
            }
        }
    }

