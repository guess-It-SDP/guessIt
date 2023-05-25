package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.ScoreActivity.Companion.SCORES_TITLE
import com.github.freeman.bootcamp.games.guessit.ScoreActivity.Companion.size
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.github.freeman.bootcamp.videocall.VideoScreen
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ScoreActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameID = intent.getStringExtra(getString(R.string.gameId_extra))
        val dbRef = getGameDBRef(this, gameID.toString())
        setContent {
            BootcampComposeTheme {
                val context = LocalContext.current

                // Get the Ids of all players in this game
                val playerIds = remember { mutableStateOf(mapOf<String, Map<String, Int>>()) }
                FirebaseUtilities.databaseGetMap(dbRef.child(context.getString(R.string.players_path)))
                    .thenAccept {
                        playerIds.value = it as HashMap<String, Map<String, Int>>
                    }

                // Get the player ID to score map
                val playersToScores = obtainPlayersToScores(dbRef, playerIds, context)

                val scores = turnIntoPairs(playersToScores)
                val usernames = fetchUserNames(scores)
                val usersToScores = usernamesToScores(scores, usernames).sortedWith(compareByDescending { it.second })

                    Column() {
                        CurrentScoreboard(usersToScores = usersToScores)
                        VideoScreen(roomName = gameID ?: "1", testing = false)
                    }
            }
        }
    }
    companion object {
        const val size = 200
    }
}

@Composable
fun CurrentScoreboard(usersToScores: List<Pair<String?, Int>>) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(Color.Blue, RoundedCornerShape(16.dp))
            .padding(8.dp)
            .border(
                BorderStroke(
                    width = 4.dp,
                    brush = Brush.linearGradient(
                        colors = FinalActivity.BLUES
                    )
                ),
                shape = RoundedCornerShape(16.dp),
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("endScoreboard")
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ScoreActivity.SCORES_TITLE,
                color = Color.White,
                style = MaterialTheme.typography.h4,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .testTag("endScoresTitle")
            )

            Spacer(modifier = Modifier.height(2.dp))
            Divider(color = FinalActivity.BLUES[1], thickness = 4.dp)

            Spacer(modifier = Modifier.height(16.dp))

            usersToScores.forEach { (name, score) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (name != null) {
                        Text(
                            text = name,
                            color = Color.White,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .testTag("end$name")
                        )
                        Text(
                            text = score.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .testTag("endScore")
                        )
                    }
                }
                Divider(color = Color.White, thickness = 1.dp)
            }
        }
    }
}