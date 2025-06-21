package com.example.capital_taxi.Presentation.viewmodel.passenger


import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capital_taxi.data.source.remote.sendChatMessage
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

data class ChatRequest(
    val text: String
)

data class ChatResponse(
    val response: String
)

class PassengerChatViewModel : ViewModel() {
    private val _messages = mutableStateListOf<Pair<String, Boolean>>()
    val messages: List<Pair<String, Boolean>> = _messages

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    fun onMessageChanged(newMessage: String) {
        _message.value = newMessage
    }

    fun sendMessage() {
        viewModelScope.launch {
            try {
                val response = sendChatMessage(_message.value)


                _messages.add(Pair(_message.value, true))

                Log.d("ChatDebug", "Sending message: ${message.value}")
// ...
                Log.d("ChatDebug", "Response from API: $response")

                if (response.isNullOrEmpty()) {
                    _messages.add(Pair("Sorry, I didn't understand you", false))
                } else {
                    _messages.add(Pair(response, false))
                }
            } catch (e: Exception) {
                _messages.add(Pair(_message.value, true))
                _messages.add(Pair("Error: ${e.localizedMessage}", false))
            } finally {
                _message.value = ""
            }
        }
    }

}
