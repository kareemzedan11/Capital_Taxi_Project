package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.FareViewModel


@Composable
fun Payment_trip_cost() {

    val  fareViewModel: FareViewModel = viewModel()
    val fare by fareViewModel.fare.observeAsState(0.0)
    Row {
        Icon(
            modifier = Modifier.size(20.dp),
            tint = Color(0XFF46C96B),
            painter = painterResource(R.drawable.dollar),
            contentDescription = "cash"
        )
        Spacer(modifier = Modifier.padding(15.dp))

        Text(
            text = "$fare EGP", fontSize = 20.sp, color = Color.Black
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = stringResource(R.string.Cash), fontSize = 20.sp, color = Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.change), fontSize = 18.sp, color = Color.Gray
        )
        Icon(
            tint = Color.Gray,
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "KeyboardArrowRight"
        )
    }
}