package com.example.capital_taxi.Presentation.ui.Passengar.Screens.payment


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capital_taxi.R

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.payment.Components.PaymentMethodCard


data class PaymentMethod(
    val name: String,
    val description: String,
    val discount: String,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController) {
    PaymentScreenContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    val context = LocalContext.current // Access the context safely
    val backgroundColor = Color(ContextCompat.getColor(context, R.color.primary_color))
    val paymentMethods = listOf(
        PaymentMethod(
            stringResource(R.string.Click),
            stringResource(R.string.Balance_250000_UZS), // Update this string in `strings.xml` if needed
            stringResource(R.string.Get_5_percent_discount), // Add this string to `strings.xml`
            R.drawable.click
        ),
        PaymentMethod(
            stringResource(R.string.Cash),
            stringResource(R.string.Prepare_your_cash), // Add this string to `strings.xml`
            stringResource(R.string.Get_7_percent_discount), // Add this string to `strings.xml`
            R.drawable.cash
        ),
        PaymentMethod(
            stringResource(R.string.Credit_card),
            stringResource(R.string.Visa_or_MasterCard), // Add this string to `strings.xml`
            stringResource(R.string.Get_5_percent_discount),
            R.drawable.card
        ),
        PaymentMethod(
            stringResource(R.string.Payme),
            stringResource(R.string.Pay_with_your_PayPal_balance), // Add this string to `strings.xml`
            stringResource(R.string.Get_3_percent_discount),
            R.drawable.paypal
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Yellow Header Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(backgroundColor),
        ) {
            // Close Icon
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp, top = 50.dp)
                    .size(40.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { navController.popBackStack() },
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.Black
            )

            // Header Text
            Text(
                stringResource(R.string.Payment_method),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 40.dp),
                fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 20.sp,
                    maxSize = 24.sp
                ),
                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.secondary_color)), // لون خفيف للتحذير
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning Icon",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.payment_message),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
//        // Payment Methods List
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
//                .background(Color.White)
//                .padding(16.dp)
//        ) {
//            item {
//                Text(
//                    stringResource(R.string.choose_payment_method), // Add this string to `strings.xml`
//                    modifier = Modifier.padding(top = 20.dp),
//                    fontSize = responsiveTextSize(
//                        fraction = 0.06f,
//                        minSize = 20.sp,
//                        maxSize = 24.sp
//                    ),
//                    fontFamily = CustomFontFamily,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//            }
//            item { Spacer(modifier = Modifier.height(20.dp)) }
//            items(paymentMethods) { method ->
//                PaymentMethodCard(
//                    paymentMethod = method,
//                    isSelected = selectedMethod == method,
//                    onSelect = { selectedMethod = method }
//                )
//            }
//            item { Spacer(modifier = Modifier.height(20.dp)) }
//            item {
//                Button(
//                    onClick = { /* Handle new method */ },
//                    shape = RoundedCornerShape(50.dp),
//                    modifier = Modifier
//                        .height(100.dp)
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp, horizontal = 30.dp),
//                    colors = ButtonDefaults.buttonColors(Color.Black)
//                ) {
//                    Text(
//                        stringResource(R.string.add_new_method_button), // Add this string to `strings.xml`
//                        fontSize = responsiveTextSize(
//                            fraction = 0.06f,
//                            minSize = 14.sp,
//                            maxSize = 18.sp
//                        ),
//                        fontFamily = CustomFontFamily,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//        }
    }
}
