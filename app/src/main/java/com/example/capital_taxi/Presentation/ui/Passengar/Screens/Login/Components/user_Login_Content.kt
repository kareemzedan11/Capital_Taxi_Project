import android.content.Context
import android.content.SharedPreferences
import LoginRequest
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Helper.PermissionViewModel
import com.example.capital_taxi.Helper.checkLocationPermission
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.Common.ForgetPassword
import com.example.capital_taxi.Presentation.Common.LoginForm
import com.example.capital_taxi.Presentation.Common.userMediaLoginOption
import com.example.capital_taxi.R


@Composable
fun userLoginContent(
    navController: NavController
) {
    val permissionViewModel: PermissionViewModel = viewModel()
    val context = LocalContext.current

    // تأكد من التحقق من الصلاحية عند تحميل الشاشة
    LaunchedEffect(context) {
        checkLocationPermission(context, permissionViewModel)
    }

    val isLocationGranted by permissionViewModel.isLocationGranted.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // SharedPreferences for storing token
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // Handle login when the button is clicked
    LaunchedEffect(email, password, isLoading) {
        if (isLoading) {
            val role = "user" // Set role as "user" by default. Change to "driver" based on app logic
            val request = LoginRequest(email = email, password = password, role = role)
            try {
                val response = LoginApiClient.loginApiService.loginuser(request)
                if (response.isSuccessful) {
                    // Get the token from the response (assuming it's in response.body().token)
                    val token = response.body()?.token

                    if (token != null) {
                        // Save token in SharedPreferences
                        editor.putString("USER_TOKEN", token)
                        editor.apply()
                        Log.d("Login", "Token: $token")
                        Log.d("SharedPreferences", "تم حفظ التوكن: $token")

                        // Navigate to the home screen on success
                        navController.navigate(Destination.UserHomeScreen.route)
                    } else {
                        loginError = "No token received"
                    }
                } else {
                    // Show error message from the response body if login fails
                    val errorMessage = response.errorBody()?.string() ?: response.message()
                    loginError = errorMessage
                }
            } catch (e: Exception) {
                // Handle error case and capture errors in the response body
                loginError = "An error occurred: ${e.localizedMessage}"
            } finally {
                // Stop the loading indicator after the operation
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.signin),
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 20.sp, maxSize = 32.sp),
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.W900,
            color = Color.Black,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        LoginForm(
            email = email,
            password = password,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.align(alignment = Alignment.End)) {
            ForgetPassword(navController)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isLocationGranted) {
                    // Start the login process when the button is clicked
                    isLoading = true
                } else {
                    navController.navigate(Destination.searchForLocation.route)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.signin),
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),
                fontFamily = CustomFontFamily,
                color = Color.Black
            )
        }

        // Show loading indicator while isLoading is true
        if (isLoading) {
            CircularProgressIndicator()
        }

        if (loginError != null) {
            // Show error message if login fails
            Text(
                text = loginError ?: "",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = stringResource(R.string.sign_in_with),
            color = Color.Black,
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 20.sp),
            fontFamily = CustomFontFamily
        )

        Spacer(modifier = Modifier.height(40.dp))
        userMediaLoginOption()

        Spacer(modifier = Modifier.height(60.dp))

        // SignUp Text
        Row {
            Text(
                text = stringResource(id = R.string.Dont_have_an_account),
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 20.sp),
                fontFamily = CustomFontFamily,
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.SignUp),
                color = colorResource(R.color.primary_color),
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 20.sp),
                fontFamily = CustomFontFamily,
                modifier = Modifier.clickable {
                    navController.navigate(Destination.UserRegister.route)
                }
            )
        }
    }
}
