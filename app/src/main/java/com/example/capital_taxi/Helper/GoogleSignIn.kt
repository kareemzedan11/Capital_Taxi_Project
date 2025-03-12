package com.example.capital_taxi.Helper


import android.content.Context
import android.content.Intent

import androidx.activity.result.ActivityResultLauncher

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

fun signInWithGoogle(
    context: Context,
    firebaseAuth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    signInLauncher: ActivityResultLauncher<Intent>
) {
    // Launch the Google sign-in intent
    val signInIntent = googleSignInClient.signInIntent
    signInLauncher.launch(signInIntent)
}fun handleSignInResult(
    task: Task<GoogleSignInAccount>,
    firebaseAuth: FirebaseAuth
) {
    try {
        val account = task.getResult(Exception::class.java)
        val idToken = account?.idToken

        // Authenticate with Firebase
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userEmail = account?.email // Get the user's email
                    println("Firebase User: ${user?.displayName}")
                    println("User Email: $userEmail") // Print the user's email
                } else {
                    println("Firebase Sign-In Failed: ${authTask.exception?.message}")
                }
            }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
