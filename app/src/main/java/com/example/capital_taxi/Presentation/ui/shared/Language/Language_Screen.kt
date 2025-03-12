import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capital_taxi.Presentation.ui.shared.Language.components.BackButton
import com.example.capital_taxi.Presentation.ui.shared.Language.components.LanguageButton
import com.example.capital_taxi.R
import com.example.capital_taxi.Presentation.ui.shared.Language.components.LanguagePreference
import com.example.capital_taxi.Presentation.ui.shared.Language.components.updateLocale
import kotlinx.coroutines.delay
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDScreen(
    navController: NavController,
    onLanguageSelected: (String) -> Unit,
    context: Context
) {
    val generalColor = colorResource(id = R.color.primary_color)
    val secondColor = colorResource(id = R.color.secondary_color)

    var selectedLanguage by remember { mutableStateOf(LanguagePreference.getSavedLanguage(context)) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        BackButton()
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.select_language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = generalColor)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LanguageButton(
                    language = stringResource(R.string.English),
                    color = secondColor,
                    painter = R.drawable.us,
                    isSelected = selectedLanguage == "en",
                    isLoading = isLoading,
                    onClick = {
                        selectedLanguage = "en"  // Immediately update selected language
                        isLoading = true  // Show loading indicator
                        onLanguageSelected("en")
                        LanguagePreference.saveLanguage(context, "en")
                        updateLocale(context, "en")
                    }
                )

                LanguageButton(
                    language = stringResource(R.string.Arabic),
                    color = secondColor,
                    painter = R.drawable.egypt,
                    isSelected = selectedLanguage == "ar",
                    isLoading = isLoading,
                    onClick = {
                        selectedLanguage = "ar"  // Immediately update selected language
                        isLoading = true  // Show loading indicator
                        onLanguageSelected("ar")
                        LanguagePreference.saveLanguage(context, "ar")
                        updateLocale(context, "ar")
                    }
                )
            }
        }
    }

    // Handle language update effect
    LaunchedEffect(selectedLanguage) {
        updateLocale(context, selectedLanguage)
    }

    // Simulate loading
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(1500)
            isLoading = false
        }
    }
}


fun updateLanguage(context: Context, languageCode: String, selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    if (selectedLanguage != languageCode) {
        LanguagePreference.saveLanguage(context, languageCode)
        onLanguageSelected(languageCode)
        updateLocale(context, languageCode)
    }
}

