package com.example.capital_taxi.Presentation.ui.Passengar.Screens.settings.Components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capital_taxi.Helper.PartialBottomSheet
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R


@Composable
fun settings(navController: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var isDarkMode by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("USER_TOKEN").apply()
    PartialBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }
    ) {
        AppearanceBottomSheetContent(isDark = isDarkMode, cancelClick = {showBottomSheet = false}) {

            isDarkMode = it
            showBottomSheet = false


        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.primary_color)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HeaderBox(title = stringResource(R.string.Settings), navController = navController)

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                SettingsSection(title = stringResource(R.string.Account_Settings)) {
                    SettingItem(
                        title = stringResource(R.string.Home),
                        icon = painterResource(R.drawable.home),
                        onClick = { navController.navigate(Destination.HomePlace.route) }
                    )
                    SettingItem(
                        title = stringResource(R.string.Add_Work),
                        icon = painterResource(R.drawable.work),
                        onClick = { navController.navigate(Destination.WorkPlace.route) }
                    )
                    SettingItem(

                        title = stringResource(R.string.Communication),
                        icon = painterResource(R.drawable.messageicon),
                        onClick = { /* Handle communication */ }
                    )
                    SettingItem(
                        title = stringResource(R.string.Saved_Places),
                        icon = painterResource(R.drawable.baseline_place_24),
                        onClick = { navController.navigate(Destination.SavedPlaces.route) }
                    )

                }
                SettingsSection(title = stringResource(R.string.Preferences)) {
                    SettingItem(
                        title = stringResource(R.string.Language),
                        icon = painterResource(R.drawable.language),
                        onClick = { navController.navigate(Destination.LanguageDScreen.route) }
                    )
                    SettingItem(
                        title = stringResource(R.string.Appearance),
                        icon = painterResource(R.drawable.mode),
                        onClick = { showBottomSheet = true }
                    )
                }
                SettingsSection(title = stringResource(R.string.Safety)) {
                    SettingItem(
                        title = stringResource(R.string.Two_Factor_Authentication),
                        icon = painterResource(R.drawable.safety),
                        onClick = { /* Handle Two-Factor Authentication */ }
                    )

                    SettingItem(
                        title = stringResource(R.string.Privacy),
                        icon = painterResource(R.drawable.settings_3524636),
                        onClick = { /* Handle Privacy Settings */ }
                    )
                    SettingItem(
                        title = stringResource(R.string.Security_Notifications),
                        icon = painterResource(R.drawable.notification),
                        onClick = { /* Handle Security Notifications */ }
                    )
                    SettingItem(
                        title = stringResource(R.string.Emergency),
                        icon = painterResource(R.drawable.emergency),
                        onClick = { /* Handle Emergency Contact */ }
                    )
                }
                SettingsSection(title = stringResource(R.string.Security_Notifications)) {
                    SettingItem(
                        title = stringResource(R.string.Logout),
                        icon = painterResource(R.drawable.logout),
                        isRed = true,
                        onClick = {
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            // توجه المستخدم لشاشة تسجيل الدخول
                            navController.navigate(Destination.UserLogin.route) {
                                popUpTo(Destination.UserHomeScreen.route) { inclusive = true } // احذف الشاشة الحالية من back stack
                            }}
                    )
                    SettingItem(
                        title = stringResource(R.string.Delete_Account),
                        isRed = true,
                        icon = painterResource(R.drawable.baseline_delete_24),
                        onClick = { /* Handle account deletion */ }
                    )
                }
            }
        }
    }
}
