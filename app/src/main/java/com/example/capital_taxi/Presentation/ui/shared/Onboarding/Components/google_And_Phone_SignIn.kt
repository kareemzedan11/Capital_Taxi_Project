package com.example.capital_taxi.Presentation.ui.shared.Onboarding.Components

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task

@Composable
fun GoogleAndPhone(onSignInClick: () -> Unit,navController: NavController,modifier: Modifier) {

    // Context for accessing resources
    val context = LocalContext.current





    // Initialize FirebaseAuth instance
    val firebaseAuth = FirebaseAuth.getInstance()

    // Configure Google Sign-In options
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    // Initialize GoogleSignInClient
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    fun handleSignInResult(task: Task<GoogleSignInAccount>, firebaseAuth: FirebaseAuth, navController: NavController) {
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




    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task, firebaseAuth, navController) // Pass navController here
    }

    // UI layout
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 20.dp)
    ) {
        ElevatedButton(
            onClick = {
                // Launch the Google sign-in intent
                val signInIntent = googleSignInClient.signInIntent
                signInLauncher.launch(signInIntent)
            },
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
             Icon(
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified,
                contentDescription = "Google icon",
                painter = painterResource(R.drawable.googleicon)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Continue with Google",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))

        }

        Spacer(modifier = Modifier.height(20.dp))

        ElevatedButton(
            onClick =  {navController.navigate("PhoneVerification")},
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.Black),
            shape = RoundedCornerShape(16.dp)
        ) {

            Icon(
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified,
                contentDescription = "phone icon",
                painter = painterResource(R.drawable.mobile)
            )
            Spacer(modifier = Modifier.weight(1f))


            Text(
                "Continue with phone",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))

        }
        Spacer(modifier = Modifier.height(20.dp))
    }

}

