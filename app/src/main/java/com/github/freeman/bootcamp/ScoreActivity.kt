package com.github.freeman.bootcamp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.ScoreActivity.Companion.size
import com.github.freeman.bootcamp.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ScoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Todo: Correctly set the game id
        val gameId = "TestGameId"
        setContent {
            BootcampComposeTheme {
                ScoreScreen(gameId)
            }
        }
    }

    companion object {
        const val size = 200
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun fetchScores(gameId: String):  List<Pair<String, Map<String, Int>>> {
    val dbRef = Firebase.database.reference
    val dbPlayersRef = dbRef.child("Games/$gameId/Players")
    val playerIds = remember { mutableStateOf(HashMap<String, Map<String, Int>>()) }

    // Get the Ids of all players in this game
    FirebaseUtilities.databaseGetMap(dbPlayersRef)
        .thenAccept {
            playerIds.value = it as HashMap<String, Map<String, Int>>
        }

    val scorePairs = ArrayList<Pair<String, Map<String, Int>>>()
    val playerToAttributes = playerIds.value.toList()
    if (playerToAttributes.isNotEmpty()) {
        for (entry in playerIds.value) {
            scorePairs.add(Pair(entry.key, entry.value))
        }
    }

    return scorePairs
}


@Composable
fun fetchUserNames(playerIds: List<Pair<String, Map<String, Int>>>): Map<String, MutableState<String>> {
    val dbRef = Firebase.database.reference
    val usernames = HashMap<String, MutableState<String>>()

    // Initialise the values of the map player ID to username
    for (entry in playerIds) {
        val id = entry.first
        usernames[id] = remember { mutableStateOf("") }//username
    }

    // Get the usernames of all players in this game
    for (entry in playerIds) {
        val id = entry.first
        FirebaseUtilities.databaseGet(dbRef.child("Profiles/$id/username"))
            .thenAccept {
                usernames[id]?.value = it
            }
    }

    return usernames
}

@Composable
fun usernamesToScores(
    scores: List<Pair<String, Map<String, Int>>>,
    usernames: Map<String, MutableState<String>>
): ArrayList<Pair<String?, Int>> {

    // Map the usernames to their corresponding scores
    val usersToScores = ArrayList<Pair<String?, Int>>()
    for (entry in scores) {
        val id = entry.first
        val score = entry.second.values.toList()[0]
        val username = usernames[id]?.value
        usersToScores.add(Pair(username, score))
    }

    return usersToScores
}

@Composable
fun ScoreScreen(gameId: String) {
    val scores = fetchScores(gameId)
    val usernames = fetchUserNames(scores)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(size.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Scoreboard(
            playerScores = usernamesToScores(scores, usernames),
            modifier = Modifier
                .width((0.475 * size).dp)
                .height((0.575 * size).dp)
        )
    }
}

@Composable
fun Scoreboard(playerScores: ArrayList<Pair<String?, Int>>, modifier: Modifier) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = modifier
            .padding(2.dp)
            .fillMaxHeight()
            .background(Color.LightGray, RoundedCornerShape(10.dp))
            .onSizeChanged { size = it }
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Scores",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(2.dp))
            playerScores.forEach { (name, score) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (name != null) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = score.toString(),
                        style = MaterialTheme.typography.body2,
                    )
                }
                Divider(color = Color.Black, thickness = 1.dp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScorePreview() {
    val gameId = "TestGameId"
    ScoreScreen(gameId)
}
