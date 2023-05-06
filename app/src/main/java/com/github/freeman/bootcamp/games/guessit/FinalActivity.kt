package com.github.freeman.bootcamp.games.guessit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class FinalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        val dbRef = getGameDBRef(this, gameId)
        setContent {
            BootcampComposeTheme {
                FinalScreen(dbRef)
            }
        }
    }
}

@Composable
fun FinalScreen(dbRef: DatabaseReference) {
    val context = LocalContext.current

    // Get the Ids of all players in this game (IDs = playerIds.value.keys)
    val playerIds = remember { mutableStateOf(mapOf<String, Map<String, Int>>()) }
    FirebaseUtilities.databaseGetMap(dbRef.child(context.getString(R.string.players_path)))
        .thenAccept {
            playerIds.value = it as HashMap<String, Map<String, Int>>
        }

    // Initialise the values of the map player ID to score
    val playersToScores = HashMap<String, MutableState<Int>>()
    for (id in playerIds.value.keys) {
        playersToScores[id] = remember { mutableStateOf(-1) }
    }

    // Fetch the points of all players to display in the end scoreboard
    for (id in playerIds.value.keys) {
        dbRef
            .child(context.getString(R.string.players_path))
            .child(id)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    updateScoreMap(playersToScores, id, snapshot)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    updateScoreMap(playersToScores, id, snapshot)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    updateScoreMap(playersToScores, id, snapshot)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    updateScoreMap(playersToScores, id, snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    // No particular action needs to be taken in this case
                }
            })
    }

    val scores = turnIntoPairs(playersToScores)
    val usernames = fetchUserNames(scores)
    val usersToScores = usernamesToScores(scores, usernames).sortedWith(compareByDescending { it.second })

    EndScoreboard(usersToScores)
}