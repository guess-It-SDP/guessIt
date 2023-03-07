package com.github.freeman.bootcamp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ChatMessageItem(chatMessage: ChatMessage) {
    Row(modifier = Modifier.padding(8.dp)) {
        Text(text = "${chatMessage.sender}: ${chatMessage.message}")
    }
}

@Composable
fun ChatMessageList(chatMessages: List<ChatMessage>) {
    LazyColumn (modifier = Modifier.fillMaxWidth()) {
        items(chatMessages) { chatMessage ->
            ChatMessageItem(chatMessage = chatMessage)
        }
    }
}

@Composable
fun ChatScreen(
    chatMessages: List<ChatMessage>,
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column {
        Text(
            text = "Chat App",
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color.Gray)
        ) {
            ChatMessageList(chatMessages = chatMessages)

        }
        //Spacer(modifier = Modifier.weight(1f))
        BottomBar(
            message = message,
            onMessageChange = onMessageChange,
            onSendClick = onSendClick
        )
    }
}




@Composable
fun BottomBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSendClick,
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "Send")
            }
        }
    }
}

@Preview
@Composable
fun MainPreview() {
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    val message = remember { mutableStateOf("") }
    val chatActive = remember { mutableStateOf(false) }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                // your other task goes here
            }
            if (chatActive.value) {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    ChatScreen(
                        chatMessages = chatMessages,
                        message = message.value,
                        onMessageChange = { message.value = it },
                        onSendClick = {
                            chatMessages.add(ChatMessage(message = message.value, sender = "me"))
                            message.value = ""
                        }
                    )
                }
            } else {
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    onClick = { chatActive.value = true }
                ) {
                    Text(text = "chat")
                }
            }
        }
    }

    BackHandler(enabled = chatActive.value) {
        chatActive.value = false
    }
}








