package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.During_the_trip
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.shadow

@Composable
fun CarAndDriverInfoCard(
    carNumber: String,
    carBrand: String,
    carColor: String,
    driverName: String,
    driverRating: Float,
    tripsCount: Int,
    carImage: Painter,
    driverImage: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        // Car Details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .height(100.dp)
                .shadow(20.dp, shape = RoundedCornerShape(50.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(50.dp)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = carImage,
                    contentDescription = "Car Image",
                    modifier = Modifier.size(130.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.weight(1f))

                    Row {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = "NUMBER", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = "BRAND", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = "COLOR", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = carNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = carBrand, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = carColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Driver Details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .shadow(20.dp, shape = RoundedCornerShape(50.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(50.dp)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Image(
                    painter = driverImage,
                    contentDescription = "Driver Image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.weight(1f))

                    Row {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = "YOUR DRIVER", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = "RATING", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = "TRIPS", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(text = driverName, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating Star",
                                tint = Color.Yellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = driverRating.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = tripsCount.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
