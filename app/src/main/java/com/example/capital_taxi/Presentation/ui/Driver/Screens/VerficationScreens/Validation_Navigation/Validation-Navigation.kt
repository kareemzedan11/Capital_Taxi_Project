package com.example.capital_taxi.Presentation.ui.Driver.Screens.VerficationScreens.Validation_Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination

@Composable
fun ValidationNavigation(navController: NavController) {
    val buttonStates = remember { mutableStateOf(listOf(false, false, false, false)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomShapeButton(
                text = "Face Validation",
                color = if (buttonStates.value[0]) Color.Green else Color.Gray,
                onClick = {
                    navController.navigate(Destination.FaceValidation.route)
                    updateButtonState(buttonStates, 0)
                }
            )

            CustomShapeButton(
                text = "Driver Licence",
                color = if (buttonStates.value[1]) Color.Green else Color.Gray,
                onClick = {
                    navController.navigate(Destination.DriverLicence.route)

                    updateButtonState(buttonStates, 1)
                }
            )

            CustomShapeButton(
                text = "National ID Validation",
                color = if (buttonStates.value[2]) Color.Green else Color.Gray,
                onClick = {
                    navController.navigate(Destination.NationalIDValidation.route)

                    updateButtonState(buttonStates, 2)
                }
            )

            CustomShapeButton(
                text = "Certificate Of Vehicle Registration",
                color = if (buttonStates.value[3]) Color.Green else Color.Gray,
                onClick = {
                    navController.navigate(Destination.CertificateOfVehicleRegistration.route)

                    updateButtonState(buttonStates, 3)
                }
            )
        }
    }
}

@Composable
fun CustomShapeButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(topStart = 50.dp, bottomEnd = 50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 16.sp),



            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}

fun updateButtonState(buttonStates: MutableState<List<Boolean>>, index: Int) {
    buttonStates.value = buttonStates.value.mapIndexed { i, completed ->
        if (i == index) true else completed
    }
}
