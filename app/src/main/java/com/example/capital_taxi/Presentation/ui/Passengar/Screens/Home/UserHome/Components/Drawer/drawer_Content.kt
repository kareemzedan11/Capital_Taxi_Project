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
import coil.compose.AsyncImage
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.navigationDrawerItem
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DriverViewModel
import com.example.capital_taxi.domain.DriverViewModelFactory
import com.example.capital_taxi.domain.RetrofitClient
import com.example.capital_taxi.domain.RetrofitClient.apiService
import com.example.capital_taxi.domain.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

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

    lateinit var ratingListener: ListenerRegistration
    var ratingSummary by remember { mutableStateOf(RatingSummary(0, 0, 0f)) }


    LaunchedEffect(driverId) {
        driverId?.let { id ->
            listenToUserRating(
                userId = id,
                onRatingUpdate = { summary ->
                    ratingSummary = summary // تحديث التقييم
                },
                onError = { e ->
                    println("فشل في الاستماع للتقييم: ${e.localizedMessage}")
                }
            )
        }
    }


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
                    AsyncImage(
                        model = userProfile?.imageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(R.drawable.person),
                        error = painterResource(R.drawable.person)
                    )

                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        Text(   text = userProfile?.name ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        RatingStars(rating = ratingSummary.average)

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


data class RatingSummary(
    val count: Int,
    val total: Int,
    val average: Float
)
fun listenToUserRating(
    userId: String,
    onRatingUpdate: (RatingSummary) -> Unit,
    onError: (Exception) -> Unit = {}
): ListenerRegistration {
    val firestore = FirebaseFirestore.getInstance()

    return firestore.collection("users")
        .whereEqualTo("id", userId)
        .limit(1)
        .addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                println("❌ Error listening to rating: ${error.localizedMessage}")
                onError(error)
                return@addSnapshotListener
            }

            if (querySnapshot != null && !querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                val ratingMap = document.get("rating") as? Map<String, Any>

                val count = (ratingMap?.get("count") as? Number)?.toInt() ?: 0
                val total = (ratingMap?.get("total") as? Number)?.toInt() ?: 0
                val average = if (count > 0) total.toFloat() / count else 0f

                onRatingUpdate(RatingSummary(count, total, average))
            } else {
                onRatingUpdate(RatingSummary(0, 0, 0f))
            }
        }
}
@Composable
fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val fullStars = rating.toInt()
        val hasHalfStar = rating - fullStars >= 0.5

        repeat(fullStars) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_star_rate_24),
                contentDescription = "Full Star",
                 tint = Color.Unspecified,// لون ذهبي
                modifier = Modifier.size(20.dp)
            )
        }

        if (hasHalfStar) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_star_half_24),
                contentDescription = "Half Star",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }

        val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0
        repeat(emptyStars) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_star_border_24),
                contentDescription = "Empty Star",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = String.format("%.1f", rating),
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
