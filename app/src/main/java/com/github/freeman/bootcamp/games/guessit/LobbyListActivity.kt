package com.github.freeman.bootcamp.games.guessit

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.databaseGet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LobbyListActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                Column {
                    TopAppbarLobbies()

                    Surface(
                        modifier = Modifier.fillMaxSize(), color = Color(0xFFF1F1F1)
                    ) {
                        LobbyList()
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppbarLobbies(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarLobbies"),
        title = {
            Text(
                text = "Lobbies",
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
    lobby: Lobby = Lobby("default id", "default lobby", 0, 0),
    backgroundColor: Color = Color.LightGray,
    onItemClick: () -> Unit = {}
) {
    val nbPlayer = remember { mutableStateOf(lobby.nbPlayer) }
    val nbPlayerRef = Firebase.database.getReference("games/${lobby.id}/parameters/nb_players")


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
            Text(
                text = "players : ${nbPlayer.value}/5",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )

        }
    }
}

@Composable
fun LobbyList() {
    val dbRef = Firebase.database.getReference("games")
    val userId = Firebase.auth.uid
    val lobbies = remember { mutableStateListOf<Lobby>() }
    val context = LocalContext.current

    dbRef.addChildEventListener(object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.value != null) {
                Toast.makeText(context, "lobby added", Toast.LENGTH_SHORT).show()
                val gameInfo = snapshot.value as HashMap<*, *>
                val id = snapshot.key!!
                try {
                    val lobbyName = gameInfo["lobby_name"] as String
                    val nbPlayer = ((gameInfo["parameters"] as HashMap<*, *>)["nb_players"] as Long).toInt()
                    val nbRounds = ((gameInfo["parameters"] as HashMap<*, *>)["nb_rounds"] as Long).toInt()

                    if (nbPlayer == 0) {
                        dbRef.child(id).removeValue()
                    } else {
                        lobbies.add(Lobby(id, lobbyName, nbPlayer, nbRounds))
                    }
                } catch (_: Exception) {

                }



            }

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Toast.makeText(context, "lobby changed", Toast.LENGTH_SHORT).show()
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            Toast.makeText(context, "lobby removed", Toast.LENGTH_SHORT).show()
            for (lobby in lobbies) {
                if (lobby.id == snapshot.key!!) {
                    lobbies.remove(lobby)
                }
            }

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Toast.makeText(context, "lobby moved", Toast.LENGTH_SHORT).show()
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "lobby cancelled", Toast.LENGTH_SHORT).show()
        }
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn {
            items(lobbies.toList()) { lobby ->
                ListItem(
                    lobby = lobby,
                    backgroundColor = Color.White,
                    onItemClick = {
//                        Log.i("LobbyList", "Info $lobby")
                        databaseGet(dbRef.child("${lobby.id}/parameters/nb_players"))
                            .thenAccept {
                                //dbRef.child("${lobby.id}/Parameters/nb_players").setValue(it.toInt() + 1)
                                dbRef.child("${lobby.id}/players/$userId/score").setValue(0)

                                val intent = Intent(context, WaitingRoomActivity::class.java)
                                    .putExtra("gameId", lobby.id)
                                context.startActivity(intent)
                            }
                    },
                )
            }
        }
    }
}

data class Lobby(val id: String, val name: String, val nbPlayer: Int, val nbRounds: Int)