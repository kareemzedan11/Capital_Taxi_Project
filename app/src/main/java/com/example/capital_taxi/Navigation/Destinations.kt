package com.example.capital_taxi.Navigation

sealed class Destination(val route: String) {
    object SplashScreen : Destination("splashScreen")
    object StartScreen : Destination("startScreen")
    object OnboardingPager : Destination("OnboardingPager")
    object PhoneVerification : Destination("PhoneVerification")
    object ConfirmInformation : Destination("ConfirmInformation")
    object UserLogin : Destination("UserLogin")
    object UserRegister : Destination("UserRegister")
    object driverLogin : Destination("driverLogin")
    object driverSignUp : Destination("DriverSignUp")
    object SelectTheMode : Destination("SelectTheMode")
    object CertificateOfVehicleRegistration : Destination("CertificateOfVehicleRegistration")
    object DriverLicence : Destination("DriverLicence")
    object FaceValidation : Destination("FaceValidation")
    object NationalIDValidation : Destination("NationalIDValidation")
    object UserHomeScreen : Destination("UserHomeScreen")

    object ValidationNavigation : Destination("ValidationNavigation")
    object PaymentScreen : Destination("PaymentScreen")
    object TripsHistoryScreen : Destination("TripsHistoryScreen")
    object voucherScreen : Destination("voucherScreen")
    object driverProfile : Destination("driverProfile")
    object userProfile : Destination("userProfile")

    object settings : Destination("settings")
    object PickupWithPickoffPoints : Destination("PickupWithPickoffPoints")
    object OtpScreen : Destination("OtpScreen")
    object SelectTheMode2 : Destination("SelectTheMode2")
    object WhereToButton : Destination("WhereToButton")
    object DriverHomeScreen : Destination("DriverHomeScreen")
    object InboxPage : Destination("InboxPage")
    object InviteFriendsPage : Destination("InviteFriendsPage")
    object IncomePage : Destination("IncomePage")
    object notification : Destination("notification")
    object driversettings : Destination("driversettings")
    object TripDetailsForDriver : Destination("TripDetailsForDriver")

    object chatbot : Destination("chatbot")
    object CapitalTaxiChatScreen : Destination("CapitalTaxiChatScreen")
    object HelpScreen : Destination("HelpScreen")
    object SafetyScreen : Destination("SafetyScreen")
    object userNotification : Destination("userNotification")
    object modeDesign : Destination("modeDesign")
    object NewPasswordScreen : Destination("NewPasswordScreen")
    object searchForLocation : Destination("searchForLocation")
    object SavedPlaces : Destination("SavedPlaces")
    object WorkPlace : Destination("WorkPlace")
    object HomePlace : Destination("HomePlace")
    object LanguageDScreen : Destination("LanguageDScreen")
    object InviteForMyApp : Destination("InviteForMyApp")
    object DriverHelpScreen : Destination("DriverHelpScreen")
    object ChatScreen : Destination("chat/{chatId}/{isDriver}/{userId}") {
        fun createRoute(chatId: String, isDriver: Boolean, userId: String): String {
            return "chat/$chatId/$isDriver/$userId"
        }
    }

    object SupportPage : Destination("SupportPage")
    object ContactSupportPage : Destination("ContactSupportPage")

    object DocumentReviewScreen : Destination("DocumentReviewScreen")



}

