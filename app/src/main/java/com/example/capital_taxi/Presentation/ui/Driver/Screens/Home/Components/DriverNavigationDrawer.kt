package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.navigationDrawerItem
import com.example.capital_taxi.R

@Composable
fun DriverNavigationDrawer(navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .background(colorResource(R.color.primary_color)),
            contentAlignment = Alignment.Center // Center the Row horizontally and vertically
        ) {
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .clickable { navController.navigate(Destination.Profile.route) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(60.dp),
                    tint = Color.Unspecified,
                    contentDescription = null,
                    painter = painterResource(R.drawable.person)
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text("Kareem", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Row {
                        // Rating Stars

                        Icon(
                            tint = Color.Unspecified,
                            contentDescription = null,
                            painter = painterResource(R.drawable.baseline_star_rate_24)
                        )

                        Spacer(Modifier.width(5.dp))
                        Text(
                            "5",
                            fontSize = responsiveTextSize(
                                fraction = 0.06f,
                                minSize = 14.sp,
                                maxSize = 16.sp
                            ),


                            fontFamily = CustomFontFamily,
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    tint = Color.Unspecified,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
                )
            }
        }

        HorizontalDivider()
        Box(modifier = Modifier.fillMaxWidth()) {

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.InboxPage.route) },

                    text = stringResource(R.string.Inbox)
                )

                Spacer(Modifier.height(10.dp))


                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.InviteFriendsPage.route) },
                    text = stringResource(R.string.Invite_Friends)
                )

                Spacer(Modifier.height(10.dp))
                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.notification.route) },
                    text = stringResource(R.string.Notifications)

                )
                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.IncomePage.route) },
                    text = stringResource(R.string.Income)
                )
                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.voucherScreen.route) },
                    text = stringResource(R.string.Wallet)
                )
                Spacer(Modifier.height(10.dp))
                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.DriverHelpScreen.route) },
                    text = stringResource(R.string.Help)
                )
                Spacer(Modifier.height(10.dp))
                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.driversettings.route) },
                    text = stringResource(R.string.Settings)
                )

            }
        }

    }
}