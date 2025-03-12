package com.example.capital_taxi.Presentation.viewmodel.shared
 

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Sign-in using Google
    fun handleSignInResult(task: Task<GoogleSignInAccount>, navController: NavController) {
        viewModelScope.launch {
            try {
                val account = task.getResult(Exception::class.java)
                val idToken = account?.idToken
                val photoUrl = account?.photoUrl?.toString()
                val displayName = account?.displayName ?: "Unknown User"
                val email = account?.email ?: "Unknown Email"

                // Authenticate with Firebase
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Navigate to ConfirmInformation screen with encoded user data
                            val encodedName = Uri.encode(displayName)
                            val encodedEmail = Uri.encode(email)
                            val encodedPhotoUrl = Uri.encode(photoUrl ?: "")
                            navController.navigate("ConfirmInformation?name=$encodedName&email=$encodedEmail&photoUrl=$encodedPhotoUrl")
                        } else {
                            // Handle authentication failure
                            println("Firebase Sign-In Failed: ${authTask.exception?.message}")
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
