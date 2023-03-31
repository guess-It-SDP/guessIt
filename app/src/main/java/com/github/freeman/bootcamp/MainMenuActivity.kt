package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.MainMenuActivity.Companion.AUDIO_REC
import com.github.freeman.bootcamp.MainMenuActivity.Companion.CHAT
import com.github.freeman.bootcamp.MainMenuActivity.Companion.DRAWING
import com.github.freeman.bootcamp.MainMenuActivity.Companion.GUESSING
import com.github.freeman.bootcamp.MainMenuActivity.Companion.PLAY
import com.github.freeman.bootcamp.MainMenuActivity.Companion.PROFILE
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SETTINGS
import com.github.freeman.bootcamp.MainMenuActivity.Companion.WORDLE
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SIGN_IN
import com.github.freeman.bootcamp.MainMenuActivity.Companion.VIDEO_CALL
import com.github.freeman.bootcamp.firebase.FirebaseUtilities.profileExists
import com.github.freeman.bootcamp.firebase.auth.FirebaseAuthActivity
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.wordle.WordleGameActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.github.freeman.bootcamp.videocall.VideoCallActivity

class MainMenuActivity : ComponentActivity() {
    private val backgroundMusicService = BackgroundMusicService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = Intent(this, backgroundMusicService::class.java)
        startService(i)
        setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
    }

    companion object {
        const val SETTINGS = "Settings"
        const val PLAY = "Play game"
        const val PROFILE = "Profile"
        const val CHAT = "Chat"
        const val GUESSING = "Guessing"
        const val AUDIO_REC = "Audio Recording"
        const val SIGN_IN = "Sign in"
        const val DRAWING = "Drawing"
        const val WORDLE = "Play Wordle"
        const val VIDEO_CALL = "Video Call"
    }
}

fun play(context: Context) {
    context.startActivity(Intent(context, GameOptionsActivity::class.java))
}


fun settings(context: Context, user: FirebaseUser?, dbRef: DatabaseReference) {
    profileExists(user, dbRef)
        .thenAccept {
            if (it) {
                context.startActivity(Intent(context, SettingsProfileActivity::class.java))
            }
        }
}

@Composable
fun SettingsButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("settingsButton"),
        onClick = { settings(context, Firebase.auth.currentUser, Firebase.database.reference) }
    ) {
        Text(SETTINGS)
    }
}

fun profile(context: Context) {
    context.startActivity(Intent(context, SettingsProfileActivity::class.java))
}


fun chatTest(context: Context) {
    context.startActivity(Intent(context, ChatActivity::class.java))
}


fun guessing(context: Context, gameId: String, answer: String) {
    context.startActivity(Intent(context, GuessingActivity::class.java).apply {
        putExtra("gameId", gameId)
    })
}

@Composable
fun GuessingButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("guessingButton"),
        onClick = {
            guessing(
                context,
                "TestGameId",
                "Flower"
            )
        } //TODO: Add the correct game ID and correct answer
    ) {
        Text(GUESSING)
    }
}

fun audioRec(context: Context) {
    context.startActivity(Intent(context, AudioRecordingActivity::class.java))
}

@Composable
fun AudioRecordingButton() {
    CreateButton(testTag = "audioRecordingButton", unit = ::audioRec, text = AUDIO_REC)
}

fun drawing(context: Context) {
    context.startActivity(Intent(context, DrawingActivity::class.java))
}

fun wordle(context: Context) {
    context.startActivity(Intent(context, WordleGameActivity::class.java))

}

fun signIn(context: Context) {
    context.startActivity(Intent(context, FirebaseAuthActivity::class.java))
}

@OptIn(ExperimentalUnsignedTypes::class)
fun videoCall(context: Context) {
    context.startActivity(Intent(context, VideoCallActivity::class.java))
}

@Composable
fun CreateButton(testTag: String, unit: (context: Context) -> Unit, text: String) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag(testTag),
        onClick = { unit(context) }
    ) {
        Text(text)
    }
}

@Composable
fun BackButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("backButton"),
        onClick = {
            context.startActivity(Intent(context, MainMenuActivity::class.java))
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back arrow icon"
        )
    }
}

@Preview
@Composable
fun BackButtonPreview() {
    BackButton()
}

/**
 * This class will be the main screen of the app
 * This class is not debugging
 */
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
            text = "Guess It!",
            fontSize = 40.sp
        )
        Spacer(modifier = Modifier.size(24.dp))
        CreateButton(testTag = "playButton", unit = ::play, text = PLAY)
        Spacer(modifier = Modifier.size(24.dp))
        SettingsButton()
        Spacer(modifier = Modifier.size(24.dp))
        CreateButton(testTag = "profileButton", unit = ::profile, text = PROFILE)
        Spacer(modifier = Modifier.size(24.dp))
        CreateButton(testTag = "chatTestButton", unit = ::chatTest, text = CHAT)
        Spacer(modifier = Modifier.size(24.dp))
        AudioRecordingButton()
        Spacer(modifier = Modifier.size(8.dp))
        GuessingButton()
        Spacer(modifier = Modifier.size(8.dp))
        CreateButton(testTag = "drawingButton", unit = ::drawing, text = DRAWING)
        Spacer(modifier = Modifier.size(8.dp))
        CreateButton(testTag = "SignInButton", unit = ::signIn, text = SIGN_IN)
        CreateButton(testTag = VIDEO_CALL, unit = ::videoCall, text = VIDEO_CALL)
        CreateButton(testTag = WORDLE, unit = ::wordle, text = WORDLE)
    }
}


@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    MainMenuScreen()
}