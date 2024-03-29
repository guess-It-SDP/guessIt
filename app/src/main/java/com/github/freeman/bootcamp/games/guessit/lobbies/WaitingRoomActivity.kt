package com.github.freeman.bootcamp.games.guessit.lobbies

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.GameManagerService
import com.github.freeman.bootcamp.games.guessit.lobbies.WaitingRoomActivity.Companion.CATEGORY_INFO
import com.github.freeman.bootcamp.games.guessit.lobbies.WaitingRoomActivity.Companion.KICKED
import com.github.freeman.bootcamp.games.guessit.lobbies.WaitingRoomActivity.Companion.NB_ROUNDS_INFO
import com.github.freeman.bootcamp.games.guessit.lobbies.WaitingRoomActivity.Companion.START_GAME
import com.github.freeman.bootcamp.games.guessit.lobbies.WaitingRoomActivity.Companion.TOPBAR_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.WaitingRoomActivity.Companion.WAITING_FOR_START_GAME
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.databaseGet
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.storageGet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

/**
 *  A screen where players wait until the host starts the game.
 *  When the activity is created, it retrieves the user's ID and the game ID from the intent that
 *  started it.
 *  It displays a top app bar with information about the room, a list of players currently in the
 *  room, and a "Start" button. The activity listens to changes in the game state and updates the
 *  UI accordingly. When the game state changes to "play game", the activity starts the appropriate
 *  activity depending on whether the user is the artist or a guesser. If the game state changes
 *  to "lobby closed", the activity removes the game from the database and finishes.
 */
class WaitingRoomActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = Firebase.auth.uid

        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()

        val database = Firebase.database.reference
        val dbRef = getGameDBRef(this, gameId)

        val storage = Firebase.storage.reference

        setContent {
            val context = LocalContext.current

            // get the host id from the database
            val hostId = remember { mutableStateOf("") }
            databaseGet(dbRef.child(getString(R.string.param_host_id_path))).thenAccept {
                hostId.value = it
            }

            val players = remember { mutableStateListOf<String>() }
            val gameStateRef = dbRef.child(getString(R.string.current_state_path))

            // starts game manager service which will handle the progress of the game
            val dbListener = gameStateRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val gameState = snapshot.getValue<String>()!!
                        if (gameState == getString(R.string.state_initialize)) {
                            val intent = Intent(context, GameManagerService::class.java)
                            intent.apply {
                                putExtra(getString(R.string.gameId_extra), gameId)
                            }
                            context.startService(intent)
                            val activity = (context as? Activity)
                            activity?.finish()
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
                Surface {
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
                            hostId = hostId.value,
                            gameId = gameId
                        )

                        StartButton(
                            dbRef = dbRef,
                            players = players
                        )
                    }
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
        const val KICKED = "You have been kicked by host"
        const val WAITING_FOR_START_GAME = "Waiting for the host to start"
    }
}

/**
 * Creates a top app bar for the waiting room screen of a game lobby. The app bar contains a title,
 * a back button with an arrow icon, and some functionality related to leaving the lobby. When the
 * back button is clicked, the function updates the database by removing the user from the players
 * list if they are not the host or ending the game if they are the host and the only player
 * remaining. It also removes the database listeners and finishes the current activity
 * (the waiting room screen) by calling the finish() method on the current activity.
 */
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 20.sp
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
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
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

/**
 * The function retrieves the name of the lobby, the selected category,
 * and the number of rounds from the Firebase Realtime Database and display it.
 */
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
            .background(MaterialTheme.colorScheme.primaryContainer)
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
                fontSize = 25.sp,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = category.value,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = nbRounds.value.toString(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/**
 * It creates a list of players with their usernames and profile pictures.
 */
@Composable
fun PlayerList(
    modifier: Modifier = Modifier,
    dbRef: DatabaseReference,
    storageRef: StorageReference,
    players: MutableCollection<String>,
    hostId: String,
    gameId: String
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
                    id = playerId,
                    picture = picture.value
                ),
                hostId = hostId,
                dbRef = dbRef,
                gameId = gameId,
                context = LocalContext.current
            )
        }
    }
}

/**
 * takes a PlayerData object as input and displays the player's name and profile picture in a row.
 */
@Composable
fun PlayerDisplay(player: PlayerData, hostId: String, dbRef: DatabaseReference, gameId: String,
                  context: Context) {
    val playerId = player.id
    val currentUserId = Firebase.auth.currentUser?.uid
    val kickedRef = dbRef
        .child(context.getString(R.string.games_path))
        .child(gameId)
        .child(context.getString(R.string.players_path))
        .child(playerId)
        .child(context.getString(R.string.kicked_path))

    kickedRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val kicked = snapshot.getValue<Boolean>()
                if (kicked != null && kicked) {
                    // Remove the given player from the player list if the player is kicked
                    dbRef
                        .child(context.getString(R.string.games_path))
                        .child(gameId)
                        .child(context.getString(R.string.players_path))
                        .child(playerId)
                        .removeValue()

                    // Send the kicked player back to the lobby list
                    if (currentUserId == playerId) {
                        Toast.makeText(context, KICKED, Toast.LENGTH_SHORT).show()
                        val activity = (context as? Activity)
                        activity?.finish()
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // No particular action needs to be taken in this case
        }
    })

    Row(
        modifier = Modifier
            .testTag("playerDisplay")
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .testTag("playerDisplay")
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
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            //  Only display the kick button if the player in question is not the host
            if (playerId != hostId) {
                val currentPlayerIsHost = currentUserId == hostId
                val visibility = if (currentPlayerIsHost) 1f else 0f

                ElevatedButton(
                    modifier = Modifier
                        .alpha(visibility) // Make the button visible for only the host
                        .testTag("kickButton$playerId"),
                    enabled = currentPlayerIsHost, // Only enable the button for the host
                    onClick = {
                        kickedRef.setValue(true)
                    },
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Image(
                        painterResource(id = R.drawable.kick_player_boot),
                        contentDescription = "Kick button icon",
                        modifier = Modifier
                            .size(20.dp)
                            .testTag("kickBoot$playerId")
                    )

                    Text(
                        text= "Kick",
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}

/**
 * used to display a button that can be clicked to start the game
 */
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
                    .setValue(context.getString(R.string.state_initialize))
                val activity = (context as? Activity)
                activity?.finish()
            },
            colors = ButtonDefaults.buttonColors()
        ) {
            if (userId == hostId.value) {
                Text(START_GAME)
            } else {
                Text(WAITING_FOR_START_GAME)
            }
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
    val id: String,
    val picture: Bitmap?
)