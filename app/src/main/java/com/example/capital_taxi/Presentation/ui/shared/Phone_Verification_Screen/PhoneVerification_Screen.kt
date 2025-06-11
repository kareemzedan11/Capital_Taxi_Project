import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.shared.OTP.Components.OtpState
import com.example.capital_taxi.Presentation.ui.shared.OTP.Components.OtpViewModel
import com.example.capital_taxi.Presentation.ui.shared.Phone_Verification_Screen.Components.CountryCodePickerView
import com.example.capital_taxi.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerification(
    navController: NavController,

) {
    val viewModel: OtpViewModel = viewModel() // ← هنا نستدعيه داخل Composable

    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+20") }
    val context = LocalContext.current
    val otpState by viewModel.otpState.collectAsState()
    // Get verificationId and phone number from SharedPreferences
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    var savedPhoneNumber = sharedPref.getString("phone_number", "")

    LaunchedEffect(otpState) {
        when (otpState) {
            is OtpState.CodeSent -> {
                val fullPhoneNumber = selectedCountryCode + phoneNumber

                // حفظ الرقم داخل SharedPreferences
                val editor = sharedPref.edit()
                editor.putString("phone_number", fullPhoneNumber)
                editor.apply()

                navController.navigate(Destination.OtpScreen.route)
            }

            is OtpState.Error -> {
                val error = (otpState as OtpState.Error).message
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phone Verification") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter your phone number",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CountryCodePicker(
                    modifier = Modifier.width(100.dp),
                    onCountryCodeSelected = { code ->
                        selectedCountryCode = code
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.sendOtp(selectedCountryCode + phoneNumber)


                },
                modifier = Modifier.fillMaxWidth(),
                enabled = phoneNumber.isNotEmpty() && otpState != OtpState.Loading
            ) {
                if (otpState == OtpState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Send OTP")
                }
            }

        }
    }
}

@Composable
fun CountryCodePicker(
    modifier: Modifier = Modifier,
    onCountryCodeSelected: (String) -> Unit
) {
    var countryCode by remember { mutableStateOf("+20") }

    AndroidView(
        factory = { context ->
            CountryCodePicker(context).apply {
                setCountryForNameCode("EG") // Default to Egypt
                setOnCountryChangeListener {
                    countryCode = selectedCountryCodeWithPlus
                    onCountryCodeSelected(countryCode)
                }
            }
        },
        modifier = modifier,
        update = { view ->
            view.setOnCountryChangeListener {
                countryCode = view.selectedCountryCodeWithPlus
                onCountryCodeSelected(countryCode)
            }
        }
    )
}