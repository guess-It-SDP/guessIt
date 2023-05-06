package com.github.freeman.bootcamp.games.guessit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.MainMenuActivity
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.BACK_TO_MENU
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.GAME_OVER
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

    companion object {
        const val GAME_OVER = "Game over!"
        const val BACK_TO_MENU = "Back to menu"
    }
}

@Composable
fun FinalScreen(dbRef: DatabaseReference) {
    val context = LocalContext.current

    // Get the Ids of all players in this game
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

    BootcampComposeTheme {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = GAME_OVER,
                fontSize = 44.sp,
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.size(30.dp))
            EndScoreboard(usersToScores)
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            BackToMenuButton(context)
        }
    }
}

@Composable
fun BackToMenuButton(context: Context) {
    ElevatedButton(
        onClick = {
            context.startActivity(Intent(context, MainMenuActivity::class.java))
        },
        modifier = Modifier.testTag("backToMenuButton")
    ) {
        Text(text = BACK_TO_MENU)
    }
}