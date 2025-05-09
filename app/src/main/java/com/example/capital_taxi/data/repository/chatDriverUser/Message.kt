package com.example.capital_taxi.data.repository.chatDriverUser

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Message(
    val id: String = "",
    val text: String = "",
    val isFromDriver: Boolean = false,
    @ServerTimestamp val timestamp: Timestamp? = null,
    val senderId: String = ""
)