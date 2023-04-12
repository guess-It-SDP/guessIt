package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.EditProfileActivity
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity.Companion.topics
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.databaseGet
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.storageGet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class WaitingRoomActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getStringExtra("gameId").toString()
//        for (i in 0 until GameOptionsActivity.NB_TOPICS) {
//            (intent.getStringExtra("topic$i").toString())
//        }


        setContent {
            BootcampComposeTheme {
                Column {
                    TopAppbarWaitingRoom()

                    Column {
                        RoomInfo(gameId = gameId)
                    }

                    PlayerList(gameId)

//                    StartButton()
                }
            }
        }
    }
}

@Composable
fun TopAppbarWaitingRoom(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarWaitingRoom"),
        title = {
            Text(
                text = "Waiting Room",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                val activity = (context as? Activity)
                activity?.finish()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}

@Composable
fun RoomInfo(modifier: Modifier = Modifier, gameId: String) {
    val dbRef = Firebase.database.getReference("Games/$gameId")

    val lobbyName = remember { mutableStateOf("") }
    val nbRounds = remember { mutableStateOf(0) }
    val category = remember { mutableStateOf("") }

    databaseGet(dbRef.child("lobby_name")).thenAccept {
        lobbyName.value = it
    }

    databaseGet(dbRef.child("Parameters/nb_rounds")).thenAccept {
        nbRounds.value = it.toInt()
    }

    databaseGet(dbRef.child("Parameters/category")).thenAccept {
        category.value = it
    }

    Column(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.LightGray)
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = lobbyName.value,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )

            Column (
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ){
                Row (
                    modifier = modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "category :",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )

                    Text(
                        text = category.value,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    )
                }


                Row (
                    modifier = modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Number of rounds :",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )

                    Text(
                        text = nbRounds.value.toString(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    )


                }

            }
        }


    }
}

@Composable
fun PlayerList(gameId: String) {
    val context = LocalContext.current
    val dbRef = Firebase.database.getReference("Games/$gameId/Players")
    val players = remember { mutableStateListOf<String>() }

    dbRef.addChildEventListener(object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Toast.makeText(context, "player added", Toast.LENGTH_SHORT).show()
            players.add(snapshot.key!!)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Toast.makeText(context, "player changed", Toast.LENGTH_SHORT).show()
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            Toast.makeText(context, "player removed", Toast.LENGTH_SHORT).show()
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Toast.makeText(context, "player moved", Toast.LENGTH_SHORT).show()
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "player cancelled", Toast.LENGTH_SHORT).show()
        }
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn {
            items(players.toList()) { playerId ->
                val picture = remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }
                val storageRef = Firebase.storage.getReference("Profiles/$playerId/picture/pic.jpg")
                LaunchedEffect(Unit) {
                    storageGet(storageRef)
                        .thenAccept {
                            picture.value = it
                        }
                }

                val username = remember { mutableStateOf("") }
                databaseGet(Firebase.database.getReference("Profiles/$playerId/username"))
                    .thenAccept {
                        username.value = it
                    }

                PlayerDisplay(
                    player = Player(
                        name = username.value,
                        picture = picture.value
                    )
                )
            }
        }
    }
}

@Composable
fun PlayerDisplay(player: Player) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("playerDisplay"),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = rememberAsyncImagePainter(player.picture),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // User's name
                Text(
                    text = player.name,
                    style = TextStyle(
                        fontSize = 22.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

data class Player(val name: String, val picture: Bitmap?)