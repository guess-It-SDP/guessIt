package com.github.freeman.bootcamp.games.guessit.lobbies

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.EditDialog
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity.Companion.DEFAULT_ID
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity.Companion.DEFAULT_LOBBY
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity.Companion.DEFAULT_NB_PLAYER
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity.Companion.DEFAULT_NB_ROUND
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity.Companion.DEFAULT_PASSWORD
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity.Companion.DEFAULT_TYPE
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity.Companion.START_SCORE
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity.Companion.TOPBAR_TEXT
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

/**
 * Shows all the available lobbies that a player can join
 */
class LobbyListActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbRef = Firebase.database.reference

        setContent {
            BootcampComposeTheme {
                val password = remember { mutableStateOf("") }
                val enterPassword = remember { mutableStateOf(false) }
                val lobby = remember { mutableStateOf(Lobby("", "", 0, 0, "", "")) }

                Column {
                    TopAppbarLobbies()

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFFF1F1F1)
                    ) {
                        if (!enterPassword.value) {
                            LobbyList(dbRef, enterPassword, lobby)
                        }
                    }

                    if (enterPassword.value) {
                        val context = LocalContext.current
                        val userId = Firebase.auth.uid
                        EditDialog(password, ENTER_PASSWORD_TEXT, PASSWORD_TEXT, enterPassword) {
                            if (lobby.value.password == it) {
                                joinLobby(context, dbRef.child(context.getString(R.string.games_path)), userId.toString(), lobby.value)
                            } else {
                                Toast.makeText(context, WRONG_PASSWORD_TEXT, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TOPBAR_TEXT = "Lobbies"
        const val DEFAULT_ID = "default id"
        const val DEFAULT_LOBBY = "default lobby"
        const val DEFAULT_NB_PLAYER = 0
        const val DEFAULT_NB_ROUND = 0
        const val DEFAULT_TYPE = "public"
        const val DEFAULT_PASSWORD = ""
        const val START_SCORE = 0
        const val ENTER_PASSWORD_TEXT = "Enter password"
        const val PASSWORD_TEXT = "password"
        const val WRONG_PASSWORD_TEXT = "Wrong password !"
    }

}

@Composable
fun TopAppbarLobbies(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarLobbies"),
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
fun ListItem(
    modifier: Modifier = Modifier,
    lobby: Lobby = Lobby(DEFAULT_ID, DEFAULT_LOBBY, DEFAULT_NB_PLAYER, DEFAULT_NB_ROUND, DEFAULT_TYPE, DEFAULT_PASSWORD),
    backgroundColor: Color = Color.LightGray,
    onItemClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val nbPlayer = remember { mutableStateOf(lobby.nbPlayer) }
    val nbPlayerRef = getGameDBRef(context, lobby.id).child(context.getString(R.string.param_nb_players_path))

    // changes dynamically the number of players in a lobby
    nbPlayerRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                nbPlayer.value = snapshot.getValue<Int>()!!
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // do nothing
        }
    })

    Column(
        modifier = modifier
            .testTag("listItem")
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(5.dp))
            .clickable { onItemClick() }
            .background(backgroundColor)

    ) {
        Row(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Column {
                Text(
                    text = lobby.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${lobby.nbRounds} rounds",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column (
                horizontalAlignment = Alignment.End
            ) {
                if(lobby.type == "private") {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = "private"
                    )
                } else {
                    // This empty text is to place the players text on the bottom
                    Text(
                        text = "",
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "players : ${nbPlayer.value}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            }

        }
    }
}

@Composable
fun LobbyList(database: DatabaseReference, enterPassword: MutableState<Boolean>, defaultLobby: MutableState<Lobby>) {
    val context = LocalContext.current
    val dbRef = database.child(context.getString(R.string.games_path))
    val userId = Firebase.auth.uid
    val lobbies = remember { mutableStateListOf<Lobby>() }

    // automatically fetches data from available lobbies in database
    dbRef.addChildEventListener(object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.value != null) {
                val gameInfo = snapshot.value as HashMap<*, *>
                val id = snapshot.key!!
                try {
                    val lobbyName = gameInfo["lobby_name"] as String
                    val nbPlayer = ((gameInfo["parameters"] as HashMap<*, *>)["nb_players"] as Long).toInt()
                    val nbRounds = ((gameInfo["parameters"] as HashMap<*, *>)["nb_rounds"] as Long).toInt()
                    val type = ((gameInfo["parameters"] as HashMap<*, *>)["type"] as String)
                    val password = ((gameInfo["parameters"] as HashMap<*, *>)["password"] as String)

                    if (nbPlayer == 0) {
                        dbRef.child(id).removeValue()
                    } else {
                        lobbies.add(Lobby(id, lobbyName, nbPlayer, nbRounds, type, password))
                    }
                } catch (_: Exception) {

                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            for (lobby in lobbies) {
                if (lobby.id == snapshot.key!!) {
                    lobbies.remove(lobby)
                }
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    })

    Column(
        modifier = Modifier
            .testTag("lobbyList")
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn {
            items(lobbies.toList()) { lobby ->
                ListItem(
                    lobby = lobby,
                    backgroundColor = Color.White,
                    onItemClick = {
                        if (lobby.type == "private") {
                            defaultLobby.value = lobby
                            enterPassword.value = true
                        } else {
                            joinLobby(context, dbRef, userId.toString(), lobby)
                        }


                        // joins a lobby
//                        joinLobby(context, dbRef, userId.toString(), lobby)
                    },
                )
            }
        }
    }
}

fun joinLobby(context: Context, dbRef: DatabaseReference, userId: String, lobby: Lobby) {
        dbRef
            .child(lobby.id)
            .child(context.getString(R.string.players_path))
            .child(userId)
            .child(context.getString(R.string.score_path))
            .setValue(START_SCORE)
        dbRef
            .child(lobby.id)
            .child(context.getString(R.string.players_path))
            .child(userId)
            .child("kicked")
            .setValue(false)

        val intent = Intent(context, WaitingRoomActivity::class.java)
            .putExtra(context.getString(R.string.gameId_extra), lobby.id)
        context.startActivity(intent)

}

/**
 * Represents a lobby item to be displayed in the lobby list
 *
 * @param id lobby id
 * @param name lobby name
 * @param nbPlayer numbers of player in the lobby
 * @param nbRounds numbers of rounds chosen for the game
 */
data class Lobby(
    val id: String,
    val name: String,
    val nbPlayer: Int,
    val nbRounds: Int,
    val type: String,
    val password: String
)