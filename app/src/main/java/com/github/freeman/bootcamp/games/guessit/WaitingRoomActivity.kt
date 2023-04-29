package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
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
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.WaitingRoomActivity.Companion.CATEGORY_INFO
import com.github.freeman.bootcamp.games.guessit.WaitingRoomActivity.Companion.NB_ROUNDS_INFO
import com.github.freeman.bootcamp.games.guessit.WaitingRoomActivity.Companion.START_GAME
import com.github.freeman.bootcamp.games.guessit.WaitingRoomActivity.Companion.TOPBAR_TEXT
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.databaseGet
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.storageGet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

/**
 * A screen where players wait until the host starts the game
 */
class WaitingRoomActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = Firebase.auth.uid

        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        val allTopics = ArrayList<String>()

        val database = Firebase.database.reference
        val dbRef = getGameDBRef(this, gameId)

        val storage = Firebase.storage.reference


        for (i in 0 until GameOptionsActivity.NB_TOPICS) {
            allTopics.add(intent.getStringExtra("topic$i").toString())
        }

        setContent {
            val context = LocalContext.current

            // get the host id from the database
            val hostId = remember { mutableStateOf("") }
            databaseGet(dbRef.child(getString(R.string.param_host_id_path))).thenAccept {
                hostId.value = it
            }

            val players = remember { mutableStateListOf<String>() }
            val gameStateRef = dbRef.child(getString(R.string.current_state_path))
            val artistRef = dbRef.child(getString(R.string.current_artist_path))

            // starts the correct activity depending on who is the artist and the guessers.
            // the host changes the game state in the database and all players listen to it
            // in order to start the game
            val dbListener = gameStateRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val gameState = snapshot.getValue<String>()!!
                        if (gameState == getString(R.string.state_playgame)) {
                            databaseGet(artistRef)
                                .thenAccept {
                                    val intent = if (userId == it) {
                                        Intent(context, TopicSelectionActivity::class.java)
                                    } else {
                                        Intent(context, GuessingActivity::class.java)
                                    }

                                    intent.apply {
                                        putExtra(getString(R.string.gameId_extra), gameId)
                                        for (i in allTopics.indices) {
                                            putExtra("topic$i", allTopics[i])
                                        }
                                    }

                                    context.startActivity(intent)
                                }
                        } else if (gameState == getString(R.string.state_lobbyclosed)) {
                            dbRef.removeValue()
                            val activity = (context as? Activity)
                            activity?.finish()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // do nothing
                }
            })


            val playersRef = dbRef.child(getString(R.string.players_path))

            // changes the number of players in the database when a player joins or leaves the game.
            // also assigns a new host if the current host leaves the game
            val playersListener = playersRef.addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key !in players) {
                        players.add(snapshot.key!!)

                        dbRef.child(getString(R.string.param_nb_players_path))
                            .setValue(players.size)
                    }

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    players.remove(snapshot.key)
                    if (snapshot.key == hostId.value && !players.isEmpty()) {
                        val newHost = players.toList()[0]
                        dbRef.child(getString(R.string.param_host_id_path))
                            .setValue(newHost)
                        dbRef.child(getString(R.string.current_artist_path))
                            .setValue(newHost)
                        hostId.value = newHost
                    }

                    dbRef.child(getString(R.string.param_nb_players_path)).setValue(players.size)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


            BootcampComposeTheme {

                Column{
                    TopAppbarWaitingRoom(
                        dbRef = dbRef,
                        hostId = hostId,
                        players = players,
                        dbListener = dbListener,
                        playersListener = playersListener
                    )

                    RoomInfo(
                        dbRef = dbRef
                    )

                    PlayerList(
                        modifier = Modifier.weight(1f),
                        dbRef = database,
                        storageRef = storage,
                        players = players,
                    )

                    StartButton(
                        dbRef = dbRef,
                        players = players
                    )
                }
            }

            // quit the lobby and updates the database when pressing back
            BackHandler {

                if (userId == hostId.value && players.size == 1) {
                    gameStateRef.setValue(getString(R.string.state_lobbyclosed))
                } else {
                    dbRef.child(getString(R.string.players_path))
                        .child(userId.toString()).removeValue()
                }

                dbRef.removeEventListener(dbListener)
                playersRef.removeEventListener(playersListener)

                val activity = (context as? Activity)
                activity?.finish()
            }
        }


    }

    companion object {
        const val TOPBAR_TEXT = "Waiting Room"
        const val CATEGORY_INFO = "Category :"
        const val NB_ROUNDS_INFO = "Number of rounds :"
        const val START_GAME = "Start"
    }
}

@Composable
fun TopAppbarWaitingRoom(
    context: Context = LocalContext.current,
    dbRef: DatabaseReference,
    hostId: MutableState<String>,
    players: MutableCollection<String>,
    dbListener: ValueEventListener,
    playersListener: ChildEventListener
) {
    val userId = Firebase.auth.uid
    val playersRef = dbRef.child(context.getString(R.string.players_path))
    val gameStateRef = dbRef.child(context.getString(R.string.current_state_path))

    TopAppBar(
        modifier = Modifier.testTag("topAppbarWaitingRoom"),
        title = {
            Text(
                text = TOPBAR_TEXT,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(
                modifier = Modifier.testTag("topAppBarBack"),
                onClick = {
                    // quit the lobby and updates the database
                    if (userId == hostId.value && players.size == 1) {
                        gameStateRef.setValue(context.getString(R.string.state_lobbyclosed))
                    } else {
                        playersRef.child(userId.toString()).removeValue()
                    }

                    dbRef.removeEventListener(dbListener)
                    playersRef.removeEventListener(playersListener)

                    val activity = (context as? Activity)
                    activity?.finish()
                }
            )
            {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}

@Composable
fun RoomInfo(
    modifier: Modifier = Modifier,
    dbRef: DatabaseReference,
) {
    val context = LocalContext.current

    val lobbyName = remember { mutableStateOf("") }
    val nbRounds = remember { mutableStateOf(0) }
    val category = remember { mutableStateOf("") }

    databaseGet(dbRef.child(context.getString(R.string.lobby_name_path))).thenAccept {
        lobbyName.value = it
    }

    databaseGet(dbRef.child(context.getString(R.string.param_nb_rounds_path))).thenAccept {
        nbRounds.value = it.toInt()
    }

    databaseGet(dbRef.child(context.getString(R.string.param_category_path))).thenAccept {
        category.value = it
    }

    Column(
        modifier = modifier
            .testTag("roomInfo")
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

            // Lobby name
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

                // selected category
                Row (
                    modifier = modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = CATEGORY_INFO,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )

                    Text(
                        text = category.value,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    )
                }

                // selected number of rounds
                Row (
                    modifier = modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = NB_ROUNDS_INFO,
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
fun PlayerList(
    modifier: Modifier = Modifier,
    dbRef: DatabaseReference,
    storageRef: StorageReference,
    players: MutableCollection<String>
) {
    val context = LocalContext.current

    LazyColumn (
        modifier = modifier
            .testTag("playerList")
    ){
        items(players.toList()) { playerId ->

            // gets the profile picture from the database
            val picture = remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }
            val profilePicRef = storageRef
                .child(context.getString(R.string.profiles_path))
                .child(playerId)
                .child(context.getString(R.string.picture_path))
            LaunchedEffect(Unit) {
                storageGet(profilePicRef)
                    .thenAccept {
                        picture.value = it
                    }
            }

            // gets the username from the database
            val username = remember { mutableStateOf("") }
            val profileUsernameRef = dbRef
                .child(context.getString(R.string.profiles_path))
                .child(playerId)
                .child(context.getString(R.string.username_path))
            databaseGet(profileUsernameRef)
                .thenAccept {
                    username.value = it
                }

            PlayerDisplay(
                player = PlayerData(
                    name = username.value,
                    picture = picture.value
                )
            )
        }
    }


}

@Composable
fun PlayerDisplay(player: PlayerData) {
    Row(
        modifier = Modifier
            .testTag("playerDisplay")
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {},
        verticalAlignment = Alignment.CenterVertically,
    ) {

        // profile picture
        Image(
            painter = rememberAsyncImagePainter(player.picture),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(10.dp)
                .clip(CircleShape)

        )

        // user's name
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

@Composable
fun StartButton(
    dbRef: DatabaseReference,
    players: MutableCollection<String>
) {
    val context = LocalContext.current

    var userId = Firebase.auth.uid
    userId = userId ?: "null"


    val hostId = remember { mutableStateOf("") }
    databaseGet(dbRef.child(context.getString(R.string.param_host_id_path))).thenAccept {
        hostId.value = it
    }



    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ){
        ElevatedButton(
            modifier = Modifier
                .testTag("startButton"),
            // the game can start if two or more players are present
            enabled = userId == hostId.value && players.size >= 2,
            onClick = {
                dbRef.child(context.getString(R.string.current_state_path))
                    .setValue(context.getString(R.string.state_playgame))
            }
        ) {
            Text(START_GAME)
        }
    }

}

/**
 * Represents the data for a player to be displayed in the player list
 *
 * @param name player name
 * @param picture player's profile picture
 */
data class PlayerData(
    val name: String,
    val picture: Bitmap?
)