package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Helper.PartialBottomSheet
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun driversettings(navController: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var isDark by remember { mutableStateOf(false) }

    PartialBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }) {


    }
    androidx.compose.material.Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                title = {
                    androidx.compose.material.Text(
                      stringResource(id = R.string.Settings),
                        fontWeight = FontWeight.Bold,
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
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                },
                backgroundColor = Color.White,
                contentColor = Color.Black
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
          ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.settings))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.15f)
                        .background(Color.White)
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(1000.dp)
                            .padding(vertical = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Scrollable Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)

                        .background(Color.White),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()), // Enable scrolling
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {

                        SettingBox(
                            title = stringResource(id = R.string.Language),
                            icon = painterResource(R.drawable.language),

                            onClick = { navController.navigate(Destination.LanguageDScreen.route) }
                        )
                        SettingBox(
                            title = stringResource(id = R.string.Preferences),

                            icon = painterResource(R.drawable.mode),

                            onClick = { showBottomSheet = true }
                        )


                        SettingBox(
                            title = stringResource(id = R.string.documents),

                            icon = painterResource(R.drawable.note),

                            onClick = { /* Handle Two-Factor Authentication */ }
                        )
                        SettingBox(
                            title = stringResource(id = R.string.terms_and_conditions),

                            icon = painterResource(R.drawable.termsandconditions),

                            onClick = { /* Handle Privacy Settings */ }
                        )
                        SettingBox(
                            title = stringResource(id = R.string.about_the_app),

                            icon = painterResource(R.drawable.abouttheapp),

                            onClick = { }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        androidx.compose.material.Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
                        ) {
                            androidx.compose.material.Text(
                                 stringResource(id = R.string.Logout),

                                color = Color.Red,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }
                }
            }
        })}


        @Composable
        fun SectionTitle(title: String) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
        }

        @Composable
        fun SettingBox(
            title: String,
            icon: Painter,
            onClick: () -> Unit,

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp) // Add spacing between items
                    .background(
                        Color(0xFFF2F2F2),
                        RoundedCornerShape(12.dp)
                    ) // Background color with rounded corners
                    .clickable { onClick() }
            ) {
                SettingOptionWithArrow(
                    title = title,
                    icon = icon,

                    onClick = onClick
                )
            }
        }

        @Composable
        fun SettingOptionWithArrow(
            title: String,

            icon: Painter,
            onClick: () -> Unit
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Larger height for more spacious options
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = icon,
                    contentDescription = title,
                    tint = colorResource(R.color.Icons_color),
                    modifier = Modifier.size(32.dp) // Larger icon size
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = title,
                    fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 20.sp),



                    fontFamily = CustomFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f) // Text takes up the remaining space
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight, // Neutral arrow icon
                    contentDescription = "Navigate",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
