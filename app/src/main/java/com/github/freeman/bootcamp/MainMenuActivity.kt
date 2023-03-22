package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SIGN_IN
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

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

fun settings(context: Context) {
    context.startActivity(Intent(context, SettingsActivity::class.java))
}

@Composable
fun SettingsButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("settingsButton"),
        onClick = { settings(context) }
    ) {
        Text(SETTINGS)
    }
}

fun profile(context: Context) {
    context.startActivity(Intent(context, ProfileActivity::class.java))
}

@Composable
fun ProfileButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("profileButton"),
        onClick = { profile(context) }
    ) {
        Text(PROFILE)
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
        Spacer(modifier = Modifier.size(50.dp))
        PlayButton()
        Spacer(modifier = Modifier.size(24.dp))
        SettingsButton()
        Spacer(modifier = Modifier.size(24.dp))
        ProfileButton()
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



@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    MainMenuScreen()
}