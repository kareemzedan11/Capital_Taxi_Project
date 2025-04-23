package com.example.capital_taxi.Presentation.ui.Passenger.Screens.ChatScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send

import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capital_taxi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message("1", "Hello, where are you?", true),
            Message("2", "I'm near the central park.", false),
            Message("3", "I'll be there in 5 minutes.", true)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // AppBar
        TopAppBar(
            title = { Text("Chat") },
            navigationIcon = {
                IconButton(onClick = {navController.popBackStack()  }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(R.color.primary_color),
                titleContentColor = Color.White
            )
        )

        // Chat Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message)
            }
        }

        // Message Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Type a message...") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (messageText.isNotBlank()) {
                        messages.add(Message((messages.size + 1).toString(), messageText, false))
                        messageText = ""
                    }
                }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor =  colorResource(R.color.primary_color),
                    unfocusedBorderColor = Color.Gray
                )
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        messages.add(Message((messages.size + 1).toString(), messageText, false))
                        messageText = ""
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = colorResource(R.color.primary_color),)
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val alignment = if (message.isFromDriver) Alignment.CenterStart else Alignment.CenterEnd
    val color = if (message.isFromDriver) Color(0xFFE0E0E0) else colorResource(R.color.Icons_color)
    val textColor = if (message.isFromDriver) Color.Black else Color.White
    val icon = if (message.isFromDriver) Icons.AutoMirrored.Filled.Send else Icons.Default.Person

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            if (message.isFromDriver) {
                Icon(icon, contentDescription = "Driver", tint = Color.Gray, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = color,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 16.sp
                    )
                }
            } else {
                Surface(
                    color = color,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(icon, contentDescription = "Passenger", tint = Color.Gray, modifier = Modifier.size(24.dp))
            }
        }
    }
}

data class Message(val id: String, val text: String, val isFromDriver: Boolean)
