package com.example.capital_taxi.Presentation.ui.Passengar.Screens.InviteFriendsPage

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.InviteFriendsPage.Components.CodeBox
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.InviteFriendsPage.Components.DetailCard
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.InviteFriendsPage.Components.ShareOption
import com.example.capital_taxi.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteForMyApp(navController: NavController) {

    val rewardItems = listOf(
        Reward(
            title = stringResource(id = R.string.your_discount),
            description = stringResource(id = R.string.get_20_percent_off)
        ),
        Reward(
            title = stringResource(id = R.string.win_a_prize),
            description = stringResource(id = R.string.get_50_gift_card)
        ),
        Reward(
            title = stringResource(id = R.string.your_code),
            description = stringResource(id = R.string.use_code_to_share)
        )
    )
    Scaffold(
        topBar = {
            TopAppBar(
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
                                contentDescription = stringResource(R.string.back),
                                tint = Color.Black
                            )
                        }
                    }
                },
                title = { Text(stringResource(R.string.Invite_Friends)) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Light background color
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Header
                Text(
                    text = stringResource(R.string.share_with_friends),
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 18.sp,
                        maxSize = 22.sp
                    ),
                    fontFamily = CustomFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.padding(30.dp))

                // Horizontal Scrollable Containers
                Text(
                    text = stringResource(R.string.rewards),
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 16.sp,
                        maxSize = 20.sp
                    ),
                    fontFamily = CustomFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(rewardItems) { reward ->
                        DetailCard(
                            title = reward.title,
                            description = reward.description,
                            backgroundColor = colorResource(R.color.secondary_color)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(30.dp))

                // Code Box Section
                Text(
                    text = stringResource(R.string.share_your_referral_code),
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 16.sp,
                        maxSize = 20.sp
                    ),
                    fontFamily = CustomFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CodeBox(code = "ABC123")

                Spacer(modifier = Modifier.padding(30.dp))

                // Share Options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ShareOption(icon = R.drawable.facelogo, label = stringResource(R.string.facebook))
                    ShareOption(icon = R.drawable.whatsapp, label = stringResource(R.string.whatsapp))
                    ShareOption(icon = R.drawable.email, label = stringResource(R.string.email))
                    ShareOption(icon = R.drawable.ic_more, label = stringResource(R.string.more))
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}



// Sample data for rewards
data class Reward(val title: String, val description: String)

val rewardItems = listOf(
    Reward("Your Discount", "Get 20% off for every friend you invite!"),
    Reward("Win a Prize", "Get a $50 gift card when 5 friends join!"),
    Reward("Your Code", "Use code: ABC123 to share!")
)

@Composable
fun RewardItemsList(): List<Reward> {
    return listOf(
        Reward(
            title = stringResource(id = R.string.your_discount),
            description = stringResource(id = R.string.get_20_percent_off)
        ),
        Reward(
            title = stringResource(id = R.string.win_a_prize),
            description = stringResource(id = R.string.get_50_gift_card)
        ),
        Reward(
            title = stringResource(id = R.string.your_code),
            description = stringResource(id = R.string.use_code_to_share)
        )
    )
}
