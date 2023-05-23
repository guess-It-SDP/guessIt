@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.freeman.bootcamp.games.guessit.chat

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.chat.ChatActivity.Companion.BOTTOMBAR_TEXT
import com.github.freeman.bootcamp.games.guessit.chat.ChatActivity.Companion.CHAT_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.chat.ChatActivity.Companion.CHAT_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

// Any activities should work with the following code for chat
class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        val dbref = getGameDBRef(this, gameId)
            .child(getString(R.string.chat_path))

        setContent {
            BootcampComposeTheme {
                Surface {
                    TopAppBarChat()
                    ChatMainScreen(dbref)
                }
            }
        }
    }

    companion object {
        const val BOTTOMBAR_TEXT = "Type a message..."
        const val CHAT_BUTTON_TEXT = "Send"
        const val CHAT_TITLE = "Global Chat"
    }

}

// Represents how the chat message is displayed
@Composable
fun ChatMessageItem(chatMessage: ChatMessage) {
    Row(modifier = Modifier
        .padding(8.dp)
        .testTag(LocalContext.current.getString(R.string.chat_message_item))) {
        Text(text = "${chatMessage.sender}: ${chatMessage.message}")
    }
}

// Scrollable layout of all messages
@Composable
fun ChatMessageList(chatMessages: Array<ChatMessage>) {
    LazyColumn (modifier = Modifier
        .fillMaxWidth()
        .testTag(LocalContext.current.getString(R.string.chat_message_list))) {
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
                .testTag(LocalContext.current.getString(R.string.chat_screen))
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
    val context = LocalContext.current

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(context.getString(R.string.chat_bottom_bar))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {
            TextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .testTag(context.getString(R.string.chat_textfield)),
                placeholder = { Text(BOTTOMBAR_TEXT) },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier
                    .testTag(context.getString(R.string.chat_send_button)),
                onClick = onSendClick,
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(
                    text = CHAT_BUTTON_TEXT,
                    maxLines = 1
                )
            }
        }
    }
}

// All chat layout including activating button
@Composable
fun ChatMainScreen(dbref: DatabaseReference) {
    var chatMessages by remember { mutableStateOf(arrayOf<ChatMessage>()) }
    var message by remember { mutableStateOf("") }

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

    val context = LocalContext.current

    //the username of the current user
    var username = ""
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val dbrefUsername = Firebase.database.reference
        .child(context.getString(R.string.profiles_path))
        .child("$uid")
        .child(context.getString(R.string.username_path))
    FirebaseUtilities.databaseGet(dbrefUsername)
        .thenAccept {
            username = it
        }

    Box(
        modifier = Modifier
            .height(300.dp)
            .fillMaxSize(),
        contentAlignment =  Alignment.BottomCenter
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
}

@Composable
fun TopAppBarChat(context: Context = LocalContext.current) {
    androidx.compose.material.TopAppBar(
        modifier = Modifier.testTag("topAppbarChat"),
        title = {
            Text(
                text = CHAT_TITLE,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        elevation = 4.dp,
        navigationIcon = {
            androidx.compose.material.IconButton(
                onClick = {
                    val activity = (context as? Activity)
                    activity?.finish()
                },
                modifier = Modifier.testTag("settingsBackButton")
            ) {
                androidx.compose.material.Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    )
}








