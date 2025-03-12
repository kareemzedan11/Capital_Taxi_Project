package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.Inbox

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R

@Composable
fun InboxPage(navController: NavController) {
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

                title = { Text("Inbox", fontWeight = FontWeight.Bold, color = Color.Black) },
                backgroundColor = Color.White,
                contentColor = Color.White
            )
        },
        content = { paddingValues ->
            InboxContent(Modifier.padding(paddingValues))
        }
    )
}

@Composable
fun InboxContent(modifier: Modifier = Modifier) {
    val inboxItems = listOf(
        "Your ride is scheduled at 10:00 AM.",
        "You have earned a reward!",
        "Update: New features are available in the app.",
        "Your payment was successful.",
        "Reminder: Complete your profile "
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(inboxItems) { item ->
            InboxItem(text = item, time = "10:00 AM")
        }
    }
}

@Composable
fun InboxItem(text: String, time: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 9.dp),
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
                    painter = painterResource(R.drawable.person1),
                    contentDescription = null,
                    tint = Color.White
                )

            }
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))

            Column {
                Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                    Text(
                        "Captain drive",
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 14.sp,
                            maxSize = 18.sp
                        ),


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
                        time,
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 9.sp,
                            maxSize = 13.sp
                        ),


                        fontFamily = CustomFontFamily,
                        fontWeight = FontWeight.W500,
                        color = Color.Gray
                    )

                }

                Spacer(modifier = Modifier.padding(vertical = 10.dp))

                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()

                        .background(Color.LightGray.copy(alpha = .4f)),


                    ) {
                    Text(
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
                        text = text,
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 12.sp,
                            maxSize = 16.sp
                        ),


                        fontFamily = CustomFontFamily,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
