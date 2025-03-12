package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationAutocompleteField(
    icon: @Composable (() -> Unit),
    onClick: () -> Unit,
    hint: String,
    query: String,
    onQueryChanged: (String) -> Unit,
    onLocationSelected: (String) -> Unit,
    apiKey: String,
    initialText: String
) {
    var isSelected by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isSelected = true

                },

            ) {
            // Icon beside the text field
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Box(
                modifier = Modifier

                    .fillMaxWidth(.9f)

                    .background(Color.Transparent)
                    .clickable {
                        onQueryChanged("")
                    },
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(

                    value = query,
                    onValueChange = {

                        isSelected = true


                        onQueryChanged(it)
                        if (it.isNotEmpty()) {
                            fetchPlaceSuggestions(it) { result ->
                                suggestions = result
                                expanded = suggestions.isNotEmpty()
                            }
                        } else {
                            suggestions = emptyList()
                            expanded = false
                        }

                    },

                    placeholder = { Text(hint) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isSelected = true

                        },
                    shape = RoundedCornerShape(0.dp), // No rounded corners
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                        cursorColor = Color.Black // Optional: Set cursor color
                    ),
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            if (isSelected&&query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .fillMaxWidth(.2f)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable {
                            onQueryChanged("")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "close Icon",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
