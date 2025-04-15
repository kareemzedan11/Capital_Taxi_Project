// OnboardingPager.kt
package com.example.capital_taxi.Presentation.ui.shared.Onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.capital_taxi.R
import com.example.capital_taxi.Navigation.Destination
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Presentation.ui.shared.Onboarding.Components.googlePhoneSignInButton
import com.example.capital_taxi.Presentation.viewmodel.shared.OnboardingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task

@Composable
fun OnboardingPager(navController: NavController) {
    val context = LocalContext.current
    val viewModel: OnboardingViewModel = viewModel()

    val backgroundColor = Color(ContextCompat.getColor(context, R.color.primary_color))

    // Google sign-in launcher
    val signInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleSignInResult(task, navController)
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.primary_color)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.primary_color)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.53f)
                    .background(colorResource(R.color.primary_color)),
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo"
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.onboarding_message1),
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.onboarding_message2),
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.sec),
                    contentDescription = "Background",
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    googlePhoneSignInButton(
                        onSignInClick = {
                            val signInIntent = GoogleSignIn.getClient(
                                context,
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .build()
                            ).signInIntent
                            signInLauncher.launch(signInIntent)
                        },
                        navController,
                        stringResource(R.string.continue_with_google),
                        image = R.drawable.googleicon
                    )

                    googlePhoneSignInButton(
                        onSignInClick = { navController.navigate(Destination.PhoneVerification.route) },
                        navController,
                        stringResource(R.string.continue_with_phone),
                        image = R.drawable.phone2
                    )

                    Text(
                        stringResource(R.string.log_in),
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 14.sp,
                            maxSize = 20.sp
                        ),
                        fontFamily = CustomFontFamily,
                        fontWeight = FontWeight.W500,
                        color = Color(0XFF987200),
                        modifier = Modifier
                            .clickable { navController.navigate(Destination.modeDesign.route) },
                    )

                    Spacer(modifier = Modifier.padding(bottom = 10.dp))
                }
            }
        }
    }
}
