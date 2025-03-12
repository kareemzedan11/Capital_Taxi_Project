package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R

@Composable
fun MostVisitedPlaces(navController: NavController){
    Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)){
        Row (modifier = Modifier.clickable {navController.navigate(Destination.HomePlace.route)  }){
            Icon(imageVector = Icons.Default.Home,
                contentDescription = "home icon")
            Spacer(modifier = Modifier.padding(start = 5.dp))

            Text(stringResource(R.string.Home))
            Spacer(modifier = Modifier.padding(start = 8.dp))

            Icon(imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "KeyboardArrowRight icon")
        }
        Spacer(modifier = Modifier.weight(1f))
        Row (modifier = Modifier.clickable {navController.navigate(Destination.WorkPlace.route)  }) {
            Icon(
                modifier = Modifier.size(20.dp),

                painter = painterResource(R.drawable.work),
                contentDescription = "work icon")
            Spacer(modifier = Modifier.padding(start = 5.dp))

            Text(stringResource(R.string.Work))

            Spacer(modifier = Modifier.padding(start = 8.dp))
            Icon(imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "KeyboardArrowRight icon")
        }
        Spacer(modifier = Modifier.weight(1f))

        Row (modifier = Modifier.clickable {navController.navigate(Destination.SavedPlaces.route)  }) {
            Icon(imageVector = Icons.Default.Star,
                contentDescription = "favorite icon")
            Spacer(modifier = Modifier.padding(start = 5.dp))

            Text(stringResource(R.string.Favorite))

            Spacer(modifier = Modifier.padding(start = 8.dp))
            Icon(imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "KeyboardArrowRight icon")
        }
    }
}