package com.github.freeman.bootcamp

import android.os.Bundle
import android.os.Debug
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.database.Database
import com.github.freeman.bootcamp.database.FirebaseDataBase
import com.github.freeman.bootcamp.database.MockDataBase
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

// This is an example of an activity class. Any activities should work with the following code
class ExampleActivity : ComponentActivity() {
    private val debug = false
    //private val db: Database = if (debug) MockDataBase() else FirebaseDataBase()
    //private val fireDb = Firebase.database.reference
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



@Composable
fun ChatMessageItem(chatMessage: ChatMessage) {
    Row(modifier = Modifier.padding(8.dp)) {
        Text(text = "${chatMessage.sender}: ${chatMessage.message}")
    }
}

@Composable
fun ChatMessageList(chatMessages: Array<ChatMessage>) {
    LazyColumn (modifier = Modifier.fillMaxWidth()) {
        items(chatMessages) { chatMessage ->
            ChatMessageItem(chatMessage = chatMessage)
        }
    }
}

@Composable
fun ChatScreen(
    chatMessages: Array<ChatMessage>,
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

@Composable
fun Main(dbref: DatabaseReference) {
    var chatMessages by remember { mutableStateOf(arrayOf<ChatMessage>()) }
    var message by remember { mutableStateOf("") }
    var chatActive by remember { mutableStateOf(false) }





    dbref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // this method is call to get the realtime
            // updates in the data.
            // this method is called when the data is
            // changed in our Firebase console.
            // below line is for getting the data from
            // snapshot of our database.
            //val value = snapshot.value
            if (snapshot.exists()) {
                val chatMessageList = snapshot.getValue<ArrayList<ChatMessage>>()!!

                // after getting the value we are setting
                // our value to message.
                //message = value.toString()

                chatMessages = chatMessageList.toTypedArray()

//            for (item in chatMessageMap!!.values) {
//                chatMessages.(item)
//            }
            }



        }

        override fun onCancelled(error: DatabaseError) {
            // calling on cancelled method when we receive
            // any error or we are not able to get the data.
            message = "fail"
        }
    })




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
                            val chtMsg = ChatMessage(message = message, sender = "me")
                            val msgId = chatMessages.size.toString()
                            dbref.child(msgId).setValue(chtMsg)
                            message = ""
                        }
                    )
                }
            } else {
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    onClick = { chatActive = true }
                ) {
                    Text(text = "chat")
                }
            }
        }
    }

    BackHandler(enabled = chatActive) {
        chatActive = false
    }
}

@Preview
@Composable
fun MainPreview() {
    val chatId = "TestChatId01"
    val dbref = Firebase.database.getReference("Chat/$chatId")
    Main(dbref)
}

@Composable
fun BackGroundComposable() {


    Text(text = "Hello World!")
}









