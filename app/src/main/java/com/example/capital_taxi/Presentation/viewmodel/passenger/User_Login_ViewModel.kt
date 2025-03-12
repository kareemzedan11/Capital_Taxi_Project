//package com.example.capital_taxi.Presentation.viewmodel.passenger
//
//import android.net.Uri
//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.navigation.NavController
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.tasks.Task
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//
//class UserLoginViewModel2 : ViewModel() {
//    private val _email = mutableStateOf("")
//    val email: State<String> get() = _email
//
//    private val _password = mutableStateOf("")
//    val password: State<String> get() = _password
//
//    private val _passwordVisible = mutableStateOf(false)
//    val passwordVisible: State<Boolean> get() = _passwordVisible
//
//    private val _isGoogleSignInVisible = mutableStateOf(false)
//    val isGoogleSignInVisible: State<Boolean> get() = _isGoogleSignInVisible
//
//    private val _isLocationGranted = mutableStateOf(false)
//    val isLocationGranted: State<Boolean> get() = _isLocationGranted
//
//    fun updateEmail(newEmail: String) {
//        _email.value = newEmail
//    }
//
//    fun updatePassword(newPassword: String) {
//        _password.value = newPassword
//    }
//
//    fun togglePasswordVisibility() {
//        _passwordVisible.value = !_passwordVisible.value
//    }
//
//    fun setGoogleSignInVisible(isVisible: Boolean) {
//        _isGoogleSignInVisible.value = isVisible
//    }
//
//    fun setLocationPermission(granted: Boolean) {
//        _isLocationGranted.value = granted
//    }
//
//    // Handle Google sign-in result and Firebase Authentication
//    fun handleSignInResult(
//        task: Task<GoogleSignInAccount>,
//        firebaseAuth: FirebaseAuth,
//        navController: NavController
//    ) {
//        try {
//            val account = task.getResult(Exception::class.java)
//            val idToken = account?.idToken
//            val photoUrl = account?.photoUrl?.toString()
//            val displayName = account?.displayName ?: "Unknown User"
//            val email = account?.email ?: "Unknown Email"
//
//            val credential = GoogleAuthProvider.getCredential(idToken, null)
//            firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener { authTask ->
//                    if (authTask.isSuccessful) {
//                        val encodedName = Uri.encode(displayName)
//                        val encodedEmail = Uri.encode(email)
//                        val encodedPhotoUrl = Uri.encode(photoUrl ?: "")
//                        navController.navigate("ConfirmInformation?name=$encodedName&email=$encodedEmail&photoUrl=$encodedPhotoUrl")
//                    } else {
//                        println("Firebase Sign-In Failed: ${authTask.exception?.message}")
//                    }
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}
