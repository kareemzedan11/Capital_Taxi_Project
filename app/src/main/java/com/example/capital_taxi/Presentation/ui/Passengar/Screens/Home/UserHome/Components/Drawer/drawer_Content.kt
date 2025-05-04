import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.navigationDrawerItem
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DriverViewModel
import com.example.capital_taxi.domain.DriverViewModelFactory
import com.example.capital_taxi.domain.RetrofitClient
import com.example.capital_taxi.domain.RetrofitClient.apiService
import com.example.capital_taxi.domain.User

@Composable
fun drawerContent(navController: NavController) {
    val apiService = RetrofitClient.apiService
    val viewModel: DriverViewModel = viewModel(factory = DriverViewModelFactory(apiService))

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE) }
    val driverId = sharedPreferences.getString("USER_ID", null)

    LaunchedEffect(Unit) {
        driverId?.let { viewModel.fetchUserProfileById(it) }
    }


    val userProfile by viewModel.userProfile.observeAsState()



    var userName by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // Add bottom padding to prevent overlap with the button
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .background(colorResource(R.color.primary_color)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable { navController.navigate(Destination.userProfile.route) },
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
                        Text(   text = userProfile?.name ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Row {
                            repeat(5) {
                                Icon(
                                    tint = Color.Unspecified,
                                    contentDescription = null,
                                    painter = painterResource(R.drawable.baseline_star_rate_24)
                                )
                            }
                            Spacer(Modifier.width(5.dp))
                            Text("5", fontSize = 16.sp)
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

            Column(
                modifier = Modifier
                    .weight(1f) // Allows scrolling content to take up remaining space
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.TripsHistoryScreen.route) },
                    painter = painterResource(R.drawable.history_3949611),
                    text = stringResource(R.string.Trip_History)
                )

                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.PaymentScreen.route) },
                    painter = painterResource(R.drawable.operation_3080541),
                    text = stringResource(R.string.Payment)
                )

                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.voucherScreen.route) },
                    painter = painterResource(R.drawable.voucher_3837379),
                    text = stringResource(R.string.Coupons)
                )

                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.userNotification.route) },
                    painter = painterResource(R.drawable.notification),
                    text = stringResource(R.string.Notifications)
                )

                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.SafetyScreen.route) },
                    painter = painterResource(R.drawable.safety),
                    text = stringResource(R.string.Safety)
                )

                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.settings.route) },
                    painter = painterResource(R.drawable.settings_3524636),
                    text = stringResource(R.string.Settings)
                )

                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.HelpScreen.route) },
                    painter = painterResource(R.drawable.headphone_18080416),
                    text = stringResource(R.string.Help)
                )

                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.SupportPage.route) },
                    painter = painterResource(R.drawable.helpme2),
                    text = stringResource(R.string.Support)
                )

                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.InviteForMyApp.route) },
                    painter = painterResource(R.drawable.invite),
                    text = stringResource(R.string.Invite_Friends)
                )
            }
        }

        // Fixed Button at the Bottom
        Button(
            onClick = { navController.navigate(Destination.UserHomeScreen.route) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.Become_driver),
                fontSize = 18.sp
            )
        }
    }
}