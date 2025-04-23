package com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen

import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.Components.ManageAccountList
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.Components.PaymentHelpSectionList
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.Components.RateFeedbackList
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.Components.RideSafetySectionList
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.Components.TripHelpSectionList
import com.example.capital_taxi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpDetailScreen(navController: NavController, topic: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topic,
                        fontWeight = FontWeight.Bold,
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 16.sp,
                            maxSize = 20.sp
                        ),
                        fontFamily = CustomFontFamily,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Transparent)
                                .border(4.dp, color = Color.Black, RoundedCornerShape(30.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(26.dp),
                                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = stringResource(R.string.back),
                                tint = Color.Black
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.Details_about, topic),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                when (topic) {
                    stringResource(id = R.string.Trips) -> TripHelpSectionList()
                    stringResource(id = R.string.Payment_and_Billing) -> PaymentHelpSectionList()
                    stringResource(id = R.string.Ride_Safety) -> RideSafetySectionList()
                    stringResource(id = R.string.Account) -> ManageAccountList()
                    stringResource(id = R.string.Rate_and_Feedback) -> RateFeedbackList()
                    else -> Text(stringResource(id = R.string.Content_coming_soon))
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(id = R.string.Was_this_helpful),
                    fontWeight = FontWeight.Medium,
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 12.sp,
                        maxSize = 16.sp
                    ),
                    fontFamily = CustomFontFamily,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { /* Handle Yes click */ },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(stringResource(id = R.string.Yes), color = Color.White)
                    }

                    Button(
                        onClick = { /* Handle No click */ },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text(stringResource(id = R.string.No), color = Color.White)
                    }
                }
            }
        }
    )
}


