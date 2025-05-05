package com.example.capital_taxi.Navigation

import DriverHelpDetailScreen
import LanguageDScreen
import PhoneVerification
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.capital_taxi.Presentation.ui.Driver.Screens.HelpScreen.DriverHelpScreen
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.Inbox.InboxPage
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.income.IncomePage
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.invite_friends.InviteFriendsPage
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.notification.notification
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.settings.driversettings
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.driverHomeScreen
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Login.DriverLoginIn
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Register.DriverSignUp
import com.example.capital_taxi.Presentation.ui.Driver.Screens.VerficationScreens.DocumentReview.DocumentReviewScreen
import com.example.capital_taxi.Presentation.ui.Driver.Screens.VerficationScreens.Validation_Navigation.ValidationNavigation
import com.example.capital_taxi.Presentation.ui.Driver.Screens.VerficationScreens.faceValidation.FaceValidation
import com.example.capital_taxi.Presentation.ui.Driver.Screens.profile.UserProfile
import com.example.capital_taxi.Presentation.ui.Driver.Screens.profile.driverProfile
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.HelpDetailScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.HelpScreen.HelpScreen

import com.example.capital_taxi.Presentation.ui.Passengar.Screens.chat_bot.CapitalTaxiChatScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.TripHistory.TripsHistoryScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.TripDestination
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.savedPlaces.HomePlace
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.savedPlaces.SavedPlaces
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.savedPlaces.WorkPlace
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.chat_bot.chatbot
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.UserHomeScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.InviteFriendsPage.InviteForMyApp
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Voucher.voucherScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.payment.PaymentScreen

import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Login.UserLogin
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Register.UserRegister
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Safety.SafetyScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Support.Components.ContactSupportPage
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Support.SupportPage
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.notification.userNotification
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.settings.Components.settings
import com.example.capital_taxi.Presentation.ui.Passenger.Screens.ChatScreen.ChatScreen
import com.example.capital_taxi.Presentation.ui.shared.Confirm_information.ConfirmInformation
import com.example.capital_taxi.Presentation.ui.shared.Language.components.updateLocale
import com.example.capital_taxi.Presentation.ui.shared.OTP.OtpScreen
 import com.example.capital_taxi.Presentation.ui.shared.Onboarding.OnboardingPager
import com.example.capital_taxi.Presentation.ui.shared.Select_the_mode.SelectTheMode
import com.example.capital_taxi.Presentation.ui.shared.Start.StartScreen
import com.example.capital_taxi.Presentation.ui.shared.modeDesign.modeDesign
import com.example.capital_taxi.Presentation.ui.shared.new_password.NewPasswordScreen
import com.example.capital_taxi.Presentation.ui.shared.search_for_location.SearchForLocation
import com.example.capital_taxi.ui.screens.Driver.VerficationScreens.CertificateOfVehicleRegistration
import com.example.capital_taxi.ui.screens.Driver.VerficationScreens.DriverLicence
import com.example.capital_taxi.ui.screens.Driver.VerficationScreens.NationalIDValidation


@Composable
fun AppNavGraph(navController: NavHostController) {

    var context = LocalContext.current
    NavHost(navController = navController, startDestination = Destination.DriverHomeScreen.route) {

        composable(Destination.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(Destination.StartScreen.route) {
            StartScreen(navController)
        }
        composable(Destination.OnboardingPager.route) {
            OnboardingPager(navController, )
        }
        composable(Destination.PhoneVerification.route) {
            PhoneVerification(navController)
        }
        composable(Destination.UserRegister.route) {
            UserRegister(navController)
        }
        composable(Destination.UserLogin.route) {
            UserLogin(navController)
        }
        composable(Destination.driverSignUp.route) {
            DriverSignUp(navController)
        }
        composable(Destination.driverLogin.route) {
            DriverLoginIn(navController)
        }
        composable(Destination.SelectTheMode.route) {
            SelectTheMode(navController)
        }
        composable(Destination.CertificateOfVehicleRegistration.route) {
            CertificateOfVehicleRegistration(navController)
        }
        composable(Destination.FaceValidation.route) {
            FaceValidation(navController)
        }
        composable(Destination.NationalIDValidation.route) {
            NationalIDValidation(navController)
        }
        composable(Destination.DriverLicence.route) {
            DriverLicence(navController)
        }
        composable(Destination.ValidationNavigation.route) {
            ValidationNavigation(navController)
        }
        composable(Destination.UserHomeScreen.route) {
            UserHomeScreen(navController)
        }
        composable(Destination.PaymentScreen.route) {
            PaymentScreen(navController)
        }
        composable(Destination.TripsHistoryScreen.route) {
            TripsHistoryScreen(navController)
        }
        composable(Destination.voucherScreen.route) {
            voucherScreen(navController)
        }
        composable(Destination.driverProfile.route) {
            driverProfile(navController)
        }
        composable(Destination.userProfile.route) {
            UserProfile(navController)
        }
        composable(Destination.settings.route) {
            settings(navController)
        }
        composable(Destination.OtpScreen.route) {
            OtpScreen(navController)
        }
        composable(Destination.WhereToButton.route) {
            TripDestination(navController)
        }
        composable(Destination.DriverHomeScreen.route) {
        driverHomeScreen(navController)
        }
        composable(Destination.InboxPage.route) {
            InboxPage(navController)
        }
        composable(Destination.InviteFriendsPage.route) {
            InviteFriendsPage(navController)
        }
        composable(Destination.IncomePage.route) {
            IncomePage(navController)
        }
        composable(Destination.notification.route) {
            notification(navController)
        }
        composable(Destination.driversettings.route) {
            driversettings(navController)
        }

        composable(Destination.chatbot.route) {
            chatbot(navController)
        }
        composable(Destination.CapitalTaxiChatScreen.route) {
            CapitalTaxiChatScreen(navController)
        }
        composable(Destination.HelpScreen.route) {
            HelpScreen(navController)
        }
        composable(Destination.SafetyScreen.route) {
            SafetyScreen(navController)
        }
        composable(Destination.userNotification.route) {
            userNotification(navController)
        }
        composable(Destination.modeDesign.route) {
            modeDesign(navController)
        }
        composable(Destination.searchForLocation.route) {
            SearchForLocation(navController)
        }
        composable(Destination.SavedPlaces.route) {
            SavedPlaces(navController)
        }
        composable(Destination.HomePlace.route) {
            HomePlace(navController)
        }
        composable(Destination.InviteForMyApp.route) {
            InviteForMyApp(navController)
        }

        composable(Destination.WorkPlace.route) {
            WorkPlace(navController)
        }

        composable(Destination.NewPasswordScreen.route) {
            NewPasswordScreen(navController)
        }
        composable(Destination.LanguageDScreen.route) {
            LanguageDScreen(navController,{}, context)
        }
        composable(Destination.DriverHelpScreen.route) {
            DriverHelpScreen(navController)
        }
        composable(Destination.DocumentReviewScreen.route) {
            DocumentReviewScreen(navController)
        }



        composable(Destination.ChatScreen.route) {
            ChatScreen(navController )
        }
        composable(Destination.SupportPage.route) {
            SupportPage(navController )
        }
        composable(Destination.ContactSupportPage.route) {
            ContactSupportPage(navController )
        }
        composable(Destination.LanguageDScreen.route) {
            val context = LocalContext.current
            LanguageDScreen(
                navController = navController,
                onLanguageSelected = { languageCode ->
                    // Handle language change here
                    updateLocale(context, languageCode)
                    (context as? Activity)?.recreate() // Recreate the activity to apply the new locale
                },
                context = context
            )
        }
        composable("help_detail/{topic}") { backStackEntry ->
          HelpDetailScreen(
                navController = navController,
                topic = backStackEntry.arguments?.getString("topic") ?: ""
            )
        }

            composable("driver_help") {
                DriverHelpScreen(navController = navController)
            }
            composable(
                "driver_help_detail/{topic}",
                arguments = listOf(navArgument("topic") { type = NavType.StringType })
            ) { backStackEntry ->
                val topic = backStackEntry.arguments?.getString("topic") ?: ""
                DriverHelpDetailScreen(navController = navController, topic = topic)
            }


        composable(
            route = "ConfirmInformation?name={name}&email={email}&photoUrl={photoUrl}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType; defaultValue = "" },
                navArgument("email") { type = NavType.StringType; defaultValue = "" },
                navArgument("photoUrl") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val photoUrl = backStackEntry.arguments?.getString("photoUrl")
            ConfirmInformation(navController, name, email, photoUrl)
        }



    }
}
