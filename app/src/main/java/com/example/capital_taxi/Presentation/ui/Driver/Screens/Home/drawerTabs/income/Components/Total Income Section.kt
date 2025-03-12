package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.income.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R


@Composable
fun TotalIncomeSection() {
    Card(elevation = 10.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(colorResource(R.color.Icons_color), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        "1 july - ",
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 14.sp,
                            maxSize = 18.sp
                        ),


                        fontFamily = CustomFontFamily,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "7 july",
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 14.sp,
                            maxSize = 18.sp
                        ),


                        fontFamily = CustomFontFamily,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    "0:00 EGB",
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 32.sp,
                        maxSize = 50.sp
                    ),


                    fontFamily = CustomFontFamily,
                    style = MaterialTheme.typography.h4,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = 20.dp,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color.White
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),

                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                " Trips",
                                fontSize = responsiveTextSize(
                                    fraction = 0.06f,
                                    minSize = 14.sp,
                                    maxSize = 18.sp
                                ),


                                fontFamily = CustomFontFamily,
                                fontWeight = FontWeight.Bold
                            )


                            Text(
                                "0",
                                fontSize = responsiveTextSize(
                                    fraction = 0.06f,
                                    minSize = 14.sp,
                                    maxSize = 16.sp
                                ),


                                fontFamily = CustomFontFamily,
                            )
                        }


                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),

                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Text(
                                " Call duration",
                                fontSize = responsiveTextSize(
                                    fraction = 0.06f,
                                    minSize = 14.sp,
                                    maxSize = 18.sp
                                ),


                                fontFamily = CustomFontFamily,
                                fontWeight = FontWeight.Bold
                            )


                            Text(
                                "0",
                                fontSize = responsiveTextSize(
                                    fraction = 0.06f,
                                    minSize = 14.sp,
                                    maxSize = 16.sp
                                ),


                                fontFamily = CustomFontFamily,
                            )

                        }
                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                " Points",
                                fontSize = responsiveTextSize(
                                    fraction = 0.06f,
                                    minSize = 14.sp,
                                    maxSize = 18.sp
                                ),


                                fontFamily = CustomFontFamily,
                                fontWeight = FontWeight.Bold
                            )


                            Text(
                                "0",
                                fontSize = responsiveTextSize(
                                    fraction = 0.06f,
                                    minSize = 14.sp,
                                    maxSize = 16.sp
                                ),


                                fontFamily = CustomFontFamily,
                            )

                        }

                    }

                }
                Spacer(modifier = Modifier.height(18.dp))

                WithdrawButton("Withdraw Earnings", colorResource(R.color.primary_color))
            }

        }
    }

}