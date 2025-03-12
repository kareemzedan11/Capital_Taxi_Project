package com.example.capital_taxi.Presentation.viewmodel.passenger


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capital_taxi.data.source.remote.sendChatMessage
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
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
            val response = sendChatMessage(_message.value)

            _messages.add(Pair(_message.value, true))

            if (response.isNullOrEmpty()) {
                _messages.add(Pair("Sorry, I didn't understand you", false))
            } else {
                _messages.add(Pair(response, false))
            }


            _message.value = ""
        }
    }
}
