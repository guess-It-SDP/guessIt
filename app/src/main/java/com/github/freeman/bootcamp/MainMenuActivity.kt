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
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SIGN_IN
import com.github.freeman.bootcamp.firebase.FirebaseUtilities.profileExists
import com.github.freeman.bootcamp.firebase.auth.FirebaseAuthActivity
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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
        const val CHAT = "Chat"
        const val GUESSING = "Guessing"
        const val AUDIO_REC = "Audio Recording"
        const val SIGN_IN = "Sign in"
        const val DRAWING = "Drawing"

    }
}

fun play(context: Context) {
    context.startActivity(Intent(context, GameOptionsActivity::class.java))
}

@Composable
fun PlayButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("playButton"),
        onClick = { play(context) }
    ) {
        Text(PLAY)
    }
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

fun chatTest(context: Context) {
    context.startActivity(Intent(context, ChatActivity::class.java))
}

@Composable
fun ChatTestButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("chatTestButton"),
        onClick = { chatTest(context) }
    ) {
        Text(CHAT)
    }
}

fun guessing(context: Context) {
    context.startActivity(Intent(context, GuessingActivity::class.java))
}

@Composable
fun GuessingButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("guessingButton"),
        onClick = { guessing(context) }
    ) {
        Text(GUESSING)
    }
}

fun audioRec(context: Context) {
    context.startActivity(Intent(context, AudioRecordingActivity::class.java))
}

@Composable
fun AudioRecordingButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("audioRecordingButton"),
        onClick = { audioRec(context) }
    ) {
        Text(AUDIO_REC)
    }
}

fun drawing(context: Context) {
    context.startActivity(Intent(context, DrawingActivity::class.java))
}

@Composable
fun DrawingButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("drawingButton"),
        onClick = { drawing(context) }
    ) {
        Text(DRAWING)
    }
}

fun signIn(context: Context) {
    context.startActivity(Intent(context, FirebaseAuthActivity::class.java))
}

@Composable
fun SignInButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("SignInButton"),
        onClick = { signIn(context) }
    ) {
        Text(SIGN_IN)
    }
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
        Spacer(modifier = Modifier.size(50.dp))
        PlayButton()
        Spacer(modifier = Modifier.size(24.dp))
        SettingsButton()
        Spacer(modifier = Modifier.size(24.dp))
        ChatTestButton()
        Spacer(modifier = Modifier.size(24.dp))
        AudioRecordingButton()
        Spacer(modifier = Modifier.size(8.dp))
        GuessingButton()
        Spacer(modifier = Modifier.size(8.dp))
        DrawingButton()
        Spacer(modifier = Modifier.size(8.dp))
        SignInButton()

    }
}

