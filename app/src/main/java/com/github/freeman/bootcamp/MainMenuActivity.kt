package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.MainMenuActivity.Companion.AUDIO_REC
import com.github.freeman.bootcamp.MainMenuActivity.Companion.CHAT
import com.github.freeman.bootcamp.MainMenuActivity.Companion.DRAWING
import com.github.freeman.bootcamp.MainMenuActivity.Companion.GUESSING
import com.github.freeman.bootcamp.MainMenuActivity.Companion.PLAY
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SETTINGS
import com.github.freeman.bootcamp.MainMenuActivity.Companion.VIDEO_CALL
import com.github.freeman.bootcamp.MainMenuActivity.Companion.WORDLE
import com.github.freeman.bootcamp.games.guessit.chat.ChatActivity
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingActivity
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.createProfile
import com.github.freeman.bootcamp.videocall.VideoCallActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * This class will be the main screen of the app
 */
class MainMenuActivity : ComponentActivity() {
    private val backgroundMusicService = BackgroundMusicService()
    private var userId = Firebase.auth.uid
    private val dbRef = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = Intent(this, backgroundMusicService::class.java)
        startService(i)
        setContent {
            val context = LocalContext.current

            // Checks if it is the first time launching the app by looking if a profile exists.
            // If no profile exists, sign in anonymously and creates a profile
            dbRef
                .child(getString(R.string.profiles_path))
                .child(userId.toString())
                .child(getString(R.string.username_path))
                .get()
                .addOnCompleteListener {

                // If no profile exists
                if (it.result.value == "" || it.result.value == null) {
                    // sign in anonymously
                    Firebase.auth.signInAnonymously().addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            userId = Firebase.auth.uid.toString()
                            createProfile(context, userId!!, "Guest")
                        }
                    }
                }
            }

            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
    }

    companion object {
        const val SETTINGS = "Settings"
        const val PLAY = "Play game"
        const val CHAT = "Chat"
        const val GUESSING = "Guessing"
        const val AUDIO_REC = "Audio Recording"
        const val DRAWING = "Drawing"
        const val WORDLE = "Play Wordle"
        const val VIDEO_CALL = "Video Call"
    }
}

@Composable
fun MainMenuButton(testTag: String, onClick: () -> Unit, text: String) {
    ElevatedButton(
        modifier = Modifier.testTag(testTag),
        onClick = onClick
    ) {
        Text(text)
    }
}


fun play(context: Context) {
    context.startActivity(Intent(context, CreateJoinActivity::class.java))
}

@Composable
fun PlayButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "playButton",
        onClick = { play(context) },
        text = PLAY
    )
}


fun settings(context: Context) {
    context.startActivity(Intent(context, SettingsProfileActivity::class.java))
}

@Composable
fun SettingsButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "settingsButton",
        onClick = { settings(context) },
        text = SETTINGS
    )
}


fun chatTest(context: Context) {
    context.startActivity(Intent(context, ChatActivity::class.java).apply {
        putExtra(context.getString(R.string.gameId_extra), context.getString(R.string.test_game_id))
    })
}

@Composable
fun ChatButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "chatButton",
        onClick = { chatTest(context) },
        text = CHAT
    )
}


fun guessing(context: Context, gameId: String) {
    context.startActivity(Intent(context, GuessingActivity::class.java).apply {
        putExtra(context.getString(R.string.gameId_extra), gameId)
    })
}

@Composable
fun GuessingButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "guessingButton",
        // TODO: Add the correct game ID and correct answer
        onClick = { guessing(context, context.getString(R.string.test_game_id)) },
        text = GUESSING
    )
}


fun audioRec(context: Context) {
    context.startActivity(Intent(context, AudioRecordingActivity::class.java))
}

@Composable
fun AudioRecordingButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "audioRecordingButton",
        onClick = { audioRec(context) },
        text = AUDIO_REC
    )
}


fun drawing(context: Context, gameId: String) {
    context.startActivity(Intent(context, DrawingActivity::class.java).apply {
        putExtra(context.getString(R.string.gameId_extra), gameId)
    })
}

@Composable
fun DrawingButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "drawingButton",
        onClick = { drawing(context, context.getString(R.string.test_game_id)) },
        text = DRAWING
    )
}


fun wordle(context: Context) {
    context.startActivity(Intent(context, WordleMenu::class.java))
}

@Composable
fun WordleButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "wordleButton",
        onClick = { wordle(context) },
        text = WORDLE
    )
}

@OptIn(ExperimentalUnsignedTypes::class)
fun videoCall(context: Context) {
    context.startActivity(Intent(context, VideoCallActivity::class.java))
}

@Composable
fun VideoCallButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "videoCallButton",
        onClick = { videoCall(context) },
        text = VIDEO_CALL
    )
}

@Composable
fun MainMenuScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mainMenuScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.testTag("gameName"),
            text = LocalContext.current.getString(R.string.app_name),
            fontSize = 40.sp
        )

        Spacer(modifier = Modifier.size(6.dp))
        PlayButton()

        Spacer(modifier = Modifier.size(6.dp))
        SettingsButton()

        Spacer(modifier = Modifier.size(6.dp))
        ChatButton()

        Spacer(modifier = Modifier.size(6.dp))
        AudioRecordingButton()

        Spacer(modifier = Modifier.size(6.dp))
        GuessingButton()

        Spacer(modifier = Modifier.size(6.dp))
        DrawingButton()

        Spacer(modifier = Modifier.size(6.dp))
        VideoCallButton()

        Spacer(modifier = Modifier.size(6.dp))
        WordleButton()
    }
}


