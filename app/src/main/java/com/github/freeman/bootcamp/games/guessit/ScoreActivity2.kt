package com.github.freeman.bootcamp.games.guessit

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.ScoreActivity2.Companion.SCORES_RECAP_BOARD_TAG
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.github.freeman.bootcamp.videocall.APP_ID
import com.github.freeman.bootcamp.videocall.VideoScreen
import com.github.freeman.bootcamp.videocall.VideoViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraVideoViewer

class ScoreActivity2 : ComponentActivity() {
    var agoraView  : AgoraVideoViewer? = null

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameID = intent.getStringExtra(getString(R.string.gameId_extra))!!
        val dbRef = getGameDBRef(this, gameID.toString())

        setContent {
            BootcampComposeTheme {
                val context = LocalContext.current
                var viewModel: VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
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
                val usersToScores = usernamesToScores(
                    scores, usernames
                ).sortedWith(compareByDescending { it.second })

                var inScoreRecap by remember { mutableStateOf(true) }
                val gamDBRef = getGameDBRef(context, gameID).child(context.getString(R.string.current_state_path))
                gamDBRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val gameState = snapshot.getValue<String>()!!
                            if (gameState != context.getString(R.string.state_scorerecap)) {
                                Log.d("VideoCall", "State scorerecap changed")
                                inScoreRecap = false
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // do nothing
                    }
                })

                Surface {
                    Column {
                        CurrentScoreboard(usersToScores = usersToScores)
                        if(inScoreRecap) {
                            val permissionLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.RequestMultiplePermissions(),
                                onResult = { perms ->
                                    viewModel.onPermissionsResult(
                                        acceptedAudioPermission = perms[Manifest.permission.RECORD_AUDIO] == true,
                                        acceptedCameraPermission = perms[Manifest.permission.CAMERA] == true,
                                    )
                                }
                            )
                            LaunchedEffect(key1 = true) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.CAMERA,
                                    )
                                )
                            }
                            BackHandler {
                                agoraView?.leaveChannel()
                                onNavigateUp()
                            }
                            if(viewModel.hasAudioPermission.value && viewModel.hasCameraPermission.value) {
                                AndroidView(
                                    factory = {
                                        AgoraVideoViewer(
                                            it, connectionData = AgoraConnectionData(
                                                appId = APP_ID
                                            ), style= AgoraVideoViewer.Style.GRID
                                        ).also {
                                            it.join(gameID)
                                            agoraView = it
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize().testTag("agora_video_view")
                                )
                            }
                        } else {
                            agoraView?.leaveChannel()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val size = 200
        const  val SCORES_RECAP_BOARD_TAG = "Scores, next round coming soon"
    }
}

@Composable
fun CurrentScoreboard(usersToScores: List<Pair<String?, Int>>) {

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
            .padding(8.dp)
            .border(
                BorderStroke(
                    width = 4.dp, brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                ),
                shape = RoundedCornerShape(16.dp),
            )
            .shadow(
                elevation = 2.dp, shape = RoundedCornerShape(16.dp)
            )
            .testTag("endScoreboard")
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ScoreActivity.SCORES_TITLE,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .testTag(SCORES_RECAP_BOARD_TAG)
            )

            Spacer(modifier = Modifier.height(2.dp))
            Divider(color = MaterialTheme.colorScheme.primary, thickness = 4.dp)

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