package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.Inbox

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

@Composable

fun InboxPage(navController: NavController ) {
    val viewModel: InboxViewModel = viewModel()
    val context = LocalContext.current

    val notifications = viewModel.notifications
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val driverId = sharedPreferences.getString("driver_id", null)

    LaunchedEffect(driverId) {
        viewModel.listenToNotifications(driverId!!)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { navController.popBackStack() }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Transparent)
                                .border(4.dp, color = Color.Black, RoundedCornerShape(30.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Icon(
                                modifier = Modifier.size(26.dp),
                                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                },
                title = {
                    Text("Inbox", fontWeight = FontWeight.Bold, color = Color.Black)
                },
                backgroundColor = Color.White,
                contentColor = Color.White
            )
        },
        content = { paddingValues ->
            InboxContent(
                modifier = Modifier.padding(paddingValues),
                notifications = notifications
            )
        }
    )
}



@Composable
fun InboxContent(
    modifier: Modifier = Modifier,
    notifications: List<NotificationData>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(notifications) { notification ->
            InboxItem(
                senderName = notification.sender,
                message = notification.message,
                time = notification.time,
                iconResId = R.drawable.person1
            )
        }
    }
}



@Composable
fun InboxItem(
    senderName: String = "Capital Taxi",
    message: String,
    time: String,
    iconResId: Int = R.drawable.person1, // يمكن تغييره حسب نوع الإشعار
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 9.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(10.dp),
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
                    .background(color = Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        senderName,
                        fontSize = responsiveTextSize(0.06f, 14.sp, 18.sp),
                        fontFamily = CustomFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.baseline_arrow_circle_left_24),
                        contentDescription = null,
                        tint = Color.Gray
                    )

                    Text(
                        text = time,
                        fontSize = responsiveTextSize(0.06f, 9.sp, 13.sp),
                        fontFamily = CustomFontFamily,
                        fontWeight = FontWeight.W500,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray.copy(alpha = .4f))
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
                        text = message,
                        fontSize = responsiveTextSize(0.06f, 12.sp, 16.sp),
                        fontFamily = CustomFontFamily,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
            }
        }
    }
}data class NotificationData(
    val sender: String,
    val message: String,
    val time: String
)

class InboxViewModel : ViewModel() {
    private val _notifications = mutableStateListOf<NotificationData>()
    val notifications: List<NotificationData> get() = _notifications

    private val db = FirebaseFirestore.getInstance()

    fun listenToNotifications(driverId: String) {
        db.collection("Inbox")
            .whereEqualTo("receiverId", driverId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                _notifications.clear()
                for (doc in snapshot.documents) {
                    val sender = doc.getString("sender") ?: "Capital Taxi"
                    val message = doc.getString("message") ?: continue

                    val timestampValue = doc.get("timestamp")
                    val time = if (timestampValue is Timestamp) {
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(timestampValue.toDate())
                    } else {
                        "Unknown"
                    }

                    _notifications.add(NotificationData(sender, message, time))
                }

            }
    }
}
