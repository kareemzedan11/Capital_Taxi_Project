package com.example.capital_taxi.Helper

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.capital_taxi.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun GoogleAuthentication(navController: NavController){
    val context= LocalContext.current
    // Initialize FirebaseAuth instance
    val firebaseAuth = FirebaseAuth.getInstance()

    // Configure Google Sign-In options
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    // Initialize GoogleSignInClient
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    fun handleSignInResult(
        task: Task<GoogleSignInAccount>,
        firebaseAuth: FirebaseAuth,
        navController: NavController
    ) {
        try {
            val account = task.getResult(Exception::class.java)
            val idToken = account?.idToken
            val photoUrl = account?.photoUrl?.toString() // Convert Uri to String
            val displayName = account?.displayName ?: "Unknown User"
            val email = account?.email ?: "Unknown Email"

            // Authenticate with Firebase
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // Encode the values for safe navigation
                        val encodedName = Uri.encode(displayName)
                        val encodedEmail = Uri.encode(email)
                        val encodedPhotoUrl = Uri.encode(photoUrl ?: "")

                        // Navigate to ConfirmInformation
                        navController.navigate("ConfirmInformation?name=$encodedName&email=$encodedEmail&photoUrl=$encodedPhotoUrl")
                    } else {
                        println("Firebase Sign-In Failed: ${authTask.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    val signInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task, firebaseAuth, navController)
        }
    // Launch the Google sign-in intent
    val signInIntent = googleSignInClient.signInIntent
    signInLauncher.launch(signInIntent)
}