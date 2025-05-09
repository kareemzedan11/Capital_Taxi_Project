
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

import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.ui.platform.LocalContext

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    rideId: String?=null,
    currentUserType: String?=null // "driver" or "passenger"
) {
    val context = LocalContext.current
    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    var messageText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val messages = remember { mutableStateListOf<Message>() }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Listen for messages in real-time
    LaunchedEffect(rideId) {
        try {
            firestore.collection("rides")
                .document(rideId!!)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        error = "Error loading messages: ${e.message}"
                        isLoading = false
                        return@addSnapshotListener
                    }

                    val newMessages = mutableListOf<Message>()
                    snapshot?.documents?.forEach { doc ->
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            newMessages.add(message)
                        }
                    }

                    messages.clear()
                    messages.addAll(newMessages)
                    isLoading = false

                    // Scroll to bottom when new message arrives
                    if (messages.isNotEmpty()) {
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(messages.size - 1)
                        }
                    }
                }
        } catch (e: Exception) {
            error = "Error setting up listener: ${e.message}"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // AppBar
        TopAppBar(
            title = { Text("Chat") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(R.color.primary_color),
                titleContentColor = Color.White
            )
        )

        // Loading/Error states
        if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(error!!, color = Color.Red)
            }
        } else {
            // Chat Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = scrollState
            ) {
                items(messages) { message ->
                    ChatBubble(
                        message = message,
                        isFromCurrentUser = message.senderType == currentUserType
                    )
                }
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
                    sendMessage(
                        rideId = rideId!!,
                        senderId = currentUserId,
                        userType = currentUserType!!,
                        text = messageText,
                        firestore = firestore
                    )
                    messageText = ""
                }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primary_color),
                    unfocusedBorderColor = Color.Gray
                )
            )

            IconButton(
                onClick = {
                    sendMessage(
                        rideId = rideId!!,
                        senderId = currentUserId,
                        userType = currentUserType!!,
                        text = messageText,
                        firestore = firestore
                    )
                    messageText = ""
                },
                modifier = Modifier.size(48.dp),
                enabled = messageText.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (messageText.isNotBlank()) colorResource(R.color.primary_color) else Color.Gray
                )
            }
        }
    }
}

private fun sendMessage(
    rideId: String,
    senderId: String,
    userType: String,
    text: String,
    firestore: FirebaseFirestore
) {
    if (text.isBlank()) return

    val message = Message(
        id = UUID.randomUUID().toString(),
        text = text,
        senderId = senderId,
        senderType = userType,
        timestamp = System.currentTimeMillis()
    )

    firestore.collection("rides")
        .document(rideId)
        .collection("messages")
        .document(message.id)
        .set(message)
        .addOnFailureListener { e ->
            // Handle error (you might want to show a snackbar)
            println("Error sending message: ${e.message}")
        }
}

@Composable
fun ChatBubble(message: Message, isFromCurrentUser: Boolean) {
    val alignment = if (isFromCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isFromCurrentUser)
        colorResource(R.color.primary_color) else
        Color(0xFFE0E0E0)
    val textColor = if (isFromCurrentUser) Color.White else Color.Black
    val icon = if (message.senderType == "driver")
        Icons.Default.Person else
        Icons.Default.Person

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            if (!isFromCurrentUser) {
                Icon(
                    icon,
                    contentDescription = "Sender",
                    tint = if (message.senderType == "driver") Color.Blue else Color.Green,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = if (isFromCurrentUser) 16.dp else 0.dp,
                    topEnd = if (isFromCurrentUser) 0.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        fontSize = 16.sp
                    )
                    Text(
                        text = formatTimestamp(message.timestamp),
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            if (isFromCurrentUser) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    icon,
                    contentDescription = "You",
                    tint = if (message.senderType == "driver") Color.Blue else Color.Green,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(date)
}

data class Message(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderType: String = "", // "driver" or "passenger"
    val timestamp: Long = 0L
)