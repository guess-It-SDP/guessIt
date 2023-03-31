@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.freeman.bootcamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

// Any activities should work with the following code for chat
class ChatActivity : ComponentActivity() {
    private val chatId = "TestChatId01" // TODO: will be set when a game is created (with intent for example)
    private val dbref = Firebase.database.getReference("Chat/$chatId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                Main(dbref)
            }
        }
    }

}

// Respresents how the chat message is displayed
@Composable
fun ChatMessageItem(chatMessage: ChatMessage) {
    Row(modifier = Modifier
        .padding(8.dp)
        .testTag("chatMessageItem")) {
        Text(text = "${chatMessage.sender}: ${chatMessage.message}")
    }
}

// Scrollable layout of all messages
@Composable
fun ChatMessageList(chatMessages: Array<ChatMessage>) {
    LazyColumn (modifier = Modifier
        .fillMaxWidth()
        .testTag("chatMessageList")) {
        items(chatMessages) { chatMessage ->
            ChatMessageItem(chatMessage = chatMessage)
        }
    }
}

// The full chat layout
@Composable
fun ChatScreen(
    chatMessages: Array<ChatMessage>,
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column {

        Box(
            modifier = Modifier
                .testTag("chatScreen")
                .weight(1f)
                .background(Color.Gray)
        ) {
            ChatMessageList(chatMessages = chatMessages)

        }
        BottomBar(
            message = message,
            onMessageChange = onMessageChange,
            onSendClick = onSendClick
        )
    }
}

// Where you write your message
@Composable
fun BottomBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bottomBar")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("textField"),
                placeholder = { Text("Type a message...") },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier
                    .testTag("sendButton"),
                onClick = onSendClick,
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "Send")
            }
        }
    }
}

// All chat layout including activating button
@Composable
fun Main(dbref: DatabaseReference) {
    var chatMessages by remember { mutableStateOf(arrayOf<ChatMessage>()) }
    var message by remember { mutableStateOf("") }
    var chatActive by remember { mutableStateOf(false) }
    // For testing purpose. Real name would be assigned with google account for example
    val randomName by remember { mutableStateOf(Random.nextInt(100, 1000)) }

    // Listens for change in the database
    dbref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val chatMessageList = snapshot.getValue<ArrayList<ChatMessage>>()!!

                chatMessages = chatMessageList.toTypedArray()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // do nothing
        }
    })

    //the username of the current user
    var username = ""
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val dbrefUsername = Firebase.database.reference.child("Profiles/$uid").child("username")
    FirebaseUtilities.databaseGet(dbrefUsername)
        .thenAccept {
            username = it
        }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                // your main task goes here
                BackGroundComposable()
            }
            if (chatActive) {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    ChatScreen(
                        chatMessages = chatMessages,
                        message = message,
                        onMessageChange = { message = it },
                        onSendClick = {
                            // Currently the Id of each msg is simply the order on which they appeared
                            val chtMsg = ChatMessage(message = message, sender = username)
                            val msgId = chatMessages.size.toString()
                            dbref.child(msgId).setValue(chtMsg)
                            message = ""
                        }
                    )
                }
            } else {
                // Button to activate chat
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .testTag("activateChatButton"),
                    onClick = { chatActive = true }
                ) {
                    Text(text = "chat")
                }
            }
        }
    }

    // Disable chat by pressing back
    BackHandler(enabled = chatActive) {
        chatActive = false
    }
}

// For testing and visualization purpose
@Preview
@Composable
fun MainPreview() {
    val chatId = "TestChatId01"
    val db = Firebase.database
    //db.useEmulator("10.0.2.2", 9000)
    val dbref = Firebase.database.getReference("Chat/$chatId")
    BootcampComposeTheme {
        Main(dbref)
    }
}

// Example composable that can be run in parallel with the chat
@Composable
fun BackGroundComposable() {
    Text(text = "Hello World!", modifier = Modifier.testTag("backGroundComposable"))
}









