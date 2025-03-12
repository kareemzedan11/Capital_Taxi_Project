package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.income.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R


@Composable
fun dataBoxList() {
    WithdrawButton("Show details", Color.LightGray)
    Spacer(modifier = Modifier.padding(vertical = 20.dp))

    dataBox("Balance", painter = painterResource(R.drawable.balance))
    Spacer(modifier = Modifier.padding(vertical = 10.dp))

    dataBox("Withdraw Method", painter = painterResource(R.drawable.bank))
    Spacer(modifier = Modifier.padding(vertical = 10.dp))

    Box(modifier = Modifier.background(Color.White)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            dataBox("Invite Offers", painter = painterResource(R.drawable.share))
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())


            dataBox("Income Opportunities", painter = painterResource(R.drawable.income))
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }
    }

    dataBox("Trip Log", painter = painterResource(R.drawable.note))
    Spacer(modifier = Modifier.padding(vertical = 10.dp))
}
