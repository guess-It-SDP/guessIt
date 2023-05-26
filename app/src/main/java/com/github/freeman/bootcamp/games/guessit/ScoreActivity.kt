package com.github.freeman.bootcamp.games.guessit

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.ScoreActivity.Companion.SCORES_TITLE
import com.github.freeman.bootcamp.games.guessit.ScoreActivity.Companion.size
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ScoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameID = intent.getStringExtra(getString(R.string.gameId_extra))
        val dbRef = getGameDBRef(this, gameID.toString())
        setContent {
            BootcampComposeTheme {
                Surface {
                    ScoreScreen(dbRef)
                }
            }
        }
    }

    companion object {
        const val size = 200
        const val SCORES_TITLE = "Scores"
        const val FINAL_SCORES_TITLE = "Final Scores"
    }
}

/***
 * This function is necessary as the scoreboard takes Pairs as input, not Maps
 */
@Composable
fun turnIntoPairs(playersToScores: Map<String, MutableState<Int>>): List<Pair<String, Int>> {
    // Converts the map of players to mutable state of int into a list of pairs ID-score instead
    val scorePairs = ArrayList<Pair<String, Int>>()
    if (playersToScores.isNotEmpty()) {
        for (entry in playersToScores) {
            scorePairs.add(Pair(entry.key, entry.value.value))
        }
    }

    return scorePairs
}

@Composable
fun fetchUserNames(playerIds: List<Pair<String, Int>>): Map<String, MutableState<String>> {
    val dbRef = Firebase.database.reference
    val usernames = HashMap<String, MutableState<String>>()

    // Initialise the values of the map player ID to username
    for (entry in playerIds) {
        val id = entry.first
        usernames[id] = remember { mutableStateOf("") }
    }

    // Get the usernames of all players in this game
    for (entry in playerIds) {
        val id = entry.first
        val drefUsername = dbRef
            .child(LocalContext.current.getString(R.string.profiles_path))
            .child(id)
            .child(LocalContext.current.getString(R.string.username_path))
        FirebaseUtilities.databaseGet(drefUsername)
            .thenAccept {
                usernames[id]?.value = it
            }
    }

    return usernames
}

@Composable
fun usernamesToScores(
    scores: List<Pair<String, Int>>,
    usernames: Map<String, MutableState<String>>
): List<Pair<String?, Int>> {

    // Map the usernames to their corresponding scores
    val usersToScores = ArrayList<Pair<String?, Int>>()
    for (entry in scores) {
        val id = entry.first
        val score = entry.second
        val username = usernames[id]?.value
        usersToScores.add(Pair(username, score))
    }

    return usersToScores
}

fun updateScoreMap(playersToScores: Map<String, MutableState<Int>>, id: String, snapshot: DataSnapshot) {
    if (snapshot.exists() && snapshot.key == "score") {
        val score = snapshot.getValue<Int>()
        if (score != null) {
            playersToScores[id]?.value = score
        }
    }
}

@Composable
fun obtainPlayersToScores(
    dbRef: DatabaseReference,
    playerIds: MutableState<Map<String, Map<String, Int>>>,
    context: Context
): HashMap<String, MutableState<Int>> {
    // Initialise the values of the map player ID to score
    val playersToScores = HashMap<String, MutableState<Int>>()
    for (id in playerIds.value.keys) {
        playersToScores[id] = remember { mutableStateOf(-1) }
    }

    // Observe the points of all players to update the scoreboard
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
    return playersToScores
}

@Composable
fun ScoreScreen(
    dbRef: DatabaseReference,
    testingPlayersToScores: HashMap<String, MutableState<Int>> = HashMap(),
    testingUsersToScores:  List<Pair<String?, Int>> = listOf()
) {
    val context = LocalContext.current

    // Get the Ids of all players in this game (IDs = playerIds.value.keys)
    val playerIds = remember { mutableStateOf(mapOf<String, Map<String, Int>>()) }
    FirebaseUtilities.databaseGetMap(dbRef.child(context.getString(R.string.players_path)))
        .thenAccept {
            playerIds.value = it as HashMap<String, Map<String, Int>>
        }

    // Get the player ID to score map
    var playersToScores = obtainPlayersToScores(dbRef, playerIds, context)

    // Dependency injection for testing purposes
    if (testingPlayersToScores.size > 0) {
        playersToScores = testingPlayersToScores
    }

    val scores = turnIntoPairs(playersToScores)
    val usernames = fetchUserNames(scores)
    var usersToScores = usernamesToScores(scores, usernames).sortedWith(compareByDescending { it.second })

    // Dependency injection for testing purposes
    if (testingPlayersToScores.size > 0) {
        usersToScores = testingUsersToScores
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Spacer(modifier = Modifier.weight(1f))

        val nbPlayers = usersToScores.size
        Scoreboard(
            playerScores = usersToScores,
            modifier = Modifier
                .width((0.475 * size).dp)
                .height(((0.225 + nbPlayers * 0.11) * size).dp)
                .testTag("scoreboard")
        )
    }
}

@Composable
fun Scoreboard(playerScores: List<Pair<String?, Int>>, modifier: Modifier) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = modifier
            .padding(2.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
            .onSizeChanged { size = it }
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxSize()
        ) {
            Text(
                text = SCORES_TITLE,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .testTag("scoresTitle"),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(2.dp))
            playerScores.forEach { (name, score) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (name != null) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .testTag(name),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            minLines = 1
                        )

                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("score"),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}