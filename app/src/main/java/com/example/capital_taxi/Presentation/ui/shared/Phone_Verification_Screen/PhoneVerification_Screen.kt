import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.shared.Phone_Verification_Screen.Components.CountryCodePickerView
import com.example.capital_taxi.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerification(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var phoneNumber by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var selectedCountry by remember { mutableStateOf("+1") }

    // Send verification code function
    fun sendVerificationCode() {
        if (phoneNumber.isNotEmpty()) {
            isVerifying = true
            val fullPhoneNumber = selectedCountry + phoneNumber
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(fullPhoneNumber) // Full phone number
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

                    override fun onVerificationFailed(e: FirebaseException) {
                        errorMessage = "Verification failed: ${e.message}"
                        isVerifying = false
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {

                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            TopAppBar(
                title = { null },
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
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.phone_verification_title),
                    fontWeight = FontWeight.W900,
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 18.sp,
                        maxSize = 24.sp
                    ),
                    fontFamily = CustomFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    stringResource(R.string.phone_verification_message),

                    fontWeight = FontWeight.W600,
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 14.sp,
                        maxSize = 18.sp
                    ),
                    fontFamily = CustomFontFamily,

                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Country Code Picker and Phone Number Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = .2f)),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    CountryCodePickerView(onCountrySelected = { selectedCountry = it })

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it.trim() },
                        label = {
                            Text(
                                stringResource(R.string.phone_number_label),
                            )
                        },
                        placeholder = { Text(stringResource(R.string.phone_number_placeholder)) },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White.copy(alpha = .2f)),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { sendVerificationCode() })
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = { navController.navigate(Destination.OtpScreen.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
                    shape = RoundedCornerShape(8.dp)

                ) {
                    Text(
                        text = stringResource(R.string.send_code_button),
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 14.sp,
                            maxSize = 18.sp
                        ),
                        fontFamily = CustomFontFamily,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (verificationId.isNotEmpty()) {
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("Enter OTP") },
                        placeholder = { Text("Enter OTP sent to your phone") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = .2f)),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            // Verification logic here
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = otp.isNotEmpty()
                    ) {
                        Text("Verify OTP")
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red, fontSize = 16.sp)
                }
            }
        }
    }
}
