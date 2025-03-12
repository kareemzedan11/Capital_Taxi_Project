package com.example.capital_taxi.Presentation.ui.shared.modeDesign

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.shared.Select_the_mode.Components.CustomBottomBackground
import com.example.capital_taxi.Presentation.ui.shared.Select_the_mode.Components.CustomTopBackground
import com.example.capital_taxi.Presentation.ui.shared.Select_the_mode.Components.ModeSelectionButton
import com.example.capital_taxi.Presentation.ui.shared.Select_the_mode.Components.SelectTheModeHeader
import com.example.capital_taxi.R

@Composable
fun modeDesign(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.primary_color).copy(alpha = .3f))
    ) {

        val generalColor = colorResource(id = R.color.primary_color)

        // Custom Background Components
        Box(modifier = Modifier.align(Alignment.TopEnd)) { CustomTopBackground(generalColor) }
        CustomBottomBackground(generalColor)

        // Main Content

        Box(
            modifier = Modifier
                .fillMaxSize(), contentAlignment = Alignment.Center

        ) {
            // Header


            // Mode Selection Buttons
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))
                SelectTheModeHeader()
                Spacer(modifier = Modifier.weight(1f))
                ModeSelectionButton(
                    label = stringResource(R.string.captain_button),
                    onClick = { navController.navigate(Destination.driverLogin.route) }
                )
                Spacer(modifier = Modifier.height(5.dp))

                ModeSelectionButton(
                    label = stringResource(R.string.passenger_button),
                    onClick = { navController.navigate(Destination.UserLogin.route) }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

        }
    }
    }