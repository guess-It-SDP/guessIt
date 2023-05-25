package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.MainMenuActivity
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.BACK_TO_MENU
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.BLUES
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.GAME_OVER
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.GAME_RECAP
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.WINNER_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.database.DatabaseReference

class FinalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        val dbRef = getGameDBRef(this, gameId)
        setContent {
            BootcampComposeTheme {
                FinalScreen(dbRef, gameId)
            }
        }
    }

    companion object {
        const val GAME_OVER = "Game over!"
        const val BACK_TO_MENU = "Back to menu"
        const val GAME_RECAP = "Game recap"
        const val WINNER_TITLE = "And the winner is… "
        val BLUES = listOf(Color(0xFF4C74C7), Color(0xFF2196F3),
            Color(0xFF03A9F4), Color(0xFF00BCD4))
    }
}

@Composable
fun FinalScreen(dbRef: DatabaseReference, gameID: String) {
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

    BootcampComposeTheme {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = GAME_OVER,
                fontSize = 44.sp,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.testTag(GAME_OVER)
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
            Row() {
                BackToMenuButton(context, dbRef)
                GameRecapButton(context, gameID, dbRef)
            }
        }
    }
}

@Composable
fun GameRecapButton(context: Context, gameID: String, dbRef: DatabaseReference){
    ElevatedButton(
        onClick = {
            val intent = Intent(context, ShareRecapActivity::class.java)
            intent.putExtra(context.getString(R.string.gameId_extra), gameID)
            context.startActivity(intent)
            val activity = (context as? Activity)
            activity?.finish()
        },
        modifier = Modifier.testTag("gameRecapButton")
    ) {
        Text(
            text = GAME_RECAP,
            modifier = Modifier.testTag(GAME_RECAP)
        )
    }
}

@Composable
fun BackToMenuButton(context: Context, dbRef: DatabaseReference) {
    ElevatedButton(
        onClick = {
            context.startActivity(Intent(context, MainMenuActivity::class.java))
            val activity = (context as? Activity)
            activity?.finish()
        },
        modifier = Modifier.testTag("backToMenuButton")
    ) {
        Text(
            text = BACK_TO_MENU,
            modifier = Modifier.testTag(BACK_TO_MENU)
        )
    }
}

@Composable
fun EndScoreboard(usersToScores: List<Pair<String?, Int>>) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .background(Color.Blue, RoundedCornerShape(16.dp))
            .padding(8.dp)
            .border(
                BorderStroke(
                    width = 4.dp,
                    brush = Brush.linearGradient(
                        colors = BLUES
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
                text = ScoreActivity.FINAL_SCORES_TITLE,
                color = Color.White,
                style = MaterialTheme.typography.h4,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .testTag("endScoresTitle")
            )

            Spacer(modifier = Modifier.height(2.dp))
            Divider(color = BLUES[1], thickness = 4.dp)

            val winner = if (usersToScores.isNotEmpty()) usersToScores[0].first else "???"
            Spacer(modifier = Modifier.height(30.dp))

            // Display the winner alongside celebration and trophy images
            Row {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.celebration),
                    contentDescription = context.getString(R.string.celebration),
                    modifier = Modifier
                        .testTag("celebration")
                        .size(40.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))

                // Vertically centers the winner text in the row
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "${WINNER_TITLE}$winner!",
                        style = MaterialTheme.typography.body1,
                        color = Color.White,
                        modifier = Modifier.testTag("winnerDeclaration")
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.trophy),
                    contentDescription = context.getString(R.string.trophy),
                    modifier = Modifier
                        .testTag("trophy")
                        .size(40.dp)
                )
            }

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
                            modifier = Modifier.weight(1f)
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