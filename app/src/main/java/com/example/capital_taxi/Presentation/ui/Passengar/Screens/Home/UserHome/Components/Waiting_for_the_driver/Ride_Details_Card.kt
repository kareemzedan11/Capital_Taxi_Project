package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDetailsCard() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,

        )
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier

                .fillMaxWidth()
                .fillMaxHeight(.5f)
            ,
            containerColor = Color.White,
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            TripDetailsBottomSheetContent()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()

            .wrapContentHeight()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = Color.Gray, // Set the border color
                shape = RoundedCornerShape(8.dp) // Optional: Rounded corners
            )
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.Start) {
            // Top Section with Title
            Text(
                text = stringResource(R.string.trip_details),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 20.sp,

                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Message Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Text Message
                Text(
                    text = "Meet at the meeting point at the stream",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),

                    modifier = Modifier.weight(1f)
                )


                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.padding(bottom = 5.dp).clickable { showBottomSheet = true },
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }



            }

        }
    }
}
