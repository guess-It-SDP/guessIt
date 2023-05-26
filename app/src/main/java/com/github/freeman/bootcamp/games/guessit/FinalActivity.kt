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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.MainMenuActivity
import com.github.freeman.bootcamp.MainMenuButton
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.BACK_TO_MENU
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.GAME_OVER
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.GAME_RECAP
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.WINNER_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FinalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        val dbRef = getGameDBRef(this, gameId)
        setContent {
            BootcampComposeTheme {
                Surface {
                    FinalScreen(dbRef, gameId)
                }
            }
        }
    }

    companion object {
        const val GAME_OVER = "Game's over!"
        const val BACK_TO_MENU = "Back to menu"
        const val GAME_RECAP = "Game recap"
        const val WINNER_TITLE = "And the winner is… "
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

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = GAME_OVER,
            fontSize = 44.sp,
            modifier = Modifier.testTag(GAME_OVER),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(50.dp))
        EndScoreboard(usersToScores)
        Spacer(modifier = Modifier.size(50.dp))

        GameRecapButton(context, gameID)
        BackToMenuButton(context)
    }
}

@Composable
fun GameRecapButton(context: Context, gameID: String){
    val displayButton = remember { mutableStateOf(false) }

    Firebase.database.reference
        .child(context.getString(R.string.games_path))
        .child(gameID)
        .child("recap_created")
        .addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getValue<Boolean>()!!) {
                        displayButton.value = true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    MainMenuButton(
        testTag = "gameRecapButton",
        onClick = {
            val intent = Intent(context, ShareRecapActivity::class.java)
            intent.putExtra(context.getString(R.string.gameId_extra), gameID)
            context.startActivity(intent)
            val activity = (context as? Activity)
            activity?.finish()
        },
        text = GAME_RECAP,
        icon = ImageVector.vectorResource(R.drawable.video),
        enabled = displayButton.value
    )
}

@Composable
fun BackToMenuButton(context: Context) {
    MainMenuButton(
        testTag = "backToMenuButton",
        onClick = {
            context.startActivity(Intent(context, MainMenuActivity::class.java))
            val activity = (context as? Activity)
            activity?.finish()
        },
        text = BACK_TO_MENU,
        icon = ImageVector.vectorResource(R.drawable.menu)
    )
}

@Composable
fun EndScoreboard(usersToScores: List<Pair<String?, Int>>) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
            .padding(8.dp)
            .border(
                BorderStroke(
                    width = 4.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
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
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .testTag("endScoresTitle"),
                text = ScoreActivity.FINAL_SCORES_TITLE,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 20.sp,
            )

            Spacer(modifier = Modifier.height(2.dp))
            Divider(color = MaterialTheme.colorScheme.primary, thickness = 4.dp)

            val winner = if (usersToScores.isNotEmpty()) usersToScores[0].first else "???"
            Spacer(modifier = Modifier.height(30.dp))

            // Display the winner alongside celebration and trophy images
            Row {
                Icon(
                    modifier = Modifier.testTag("celebration1").size(40.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.guessitvictory),
                    contentDescription = context.getString(R.string.trophy),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))

                // Vertically centers the winner text in the row
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "${WINNER_TITLE}\n$winner!",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.testTag("winnerDeclaration"),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    modifier = Modifier.testTag("celebration2").size(40.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.guessitvictory),
                    contentDescription = context.getString(R.string.trophy),
                    tint = MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .testTag("end$name")
                        )

                        Text(
                            text = score.toString(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .testTag("endScore")
                        )
                    }
                }
                Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
            }
        }
    }
}