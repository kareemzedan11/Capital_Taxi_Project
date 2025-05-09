package com.example.capital_taxi.data.repository.chatDriverUser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private var messagesListener: ListenerRegistration? = null

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    fun loadMessages(chatId: String) {
        _isLoading.value = true
        messagesListener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                error?.let {
                    _error.value = it.message
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val messagesList = it.documents.map { doc ->
                        Message(
                            id = doc.id,
                            text = doc.getString("text") ?: "",
                            isFromDriver = doc.getBoolean("isFromDriver") ?: false,
                            timestamp = doc.getTimestamp("timestamp"),
                            senderId = doc.getString("senderId") ?: ""
                        )
                    }
                    _messages.value = messagesList
                }
            }
    }

    fun sendMessage(chatId: String, text: String, isFromDriver: Boolean, senderId: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val message = hashMapOf(
                    "text" to text,
                    "isFromDriver" to isFromDriver,
                    "timestamp" to Timestamp.now(),
                    "senderId" to senderId
                )

                firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .add(message)
                    .await()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearChat() {
        messagesListener?.remove()
        _messages.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        clearChat()
    }
}