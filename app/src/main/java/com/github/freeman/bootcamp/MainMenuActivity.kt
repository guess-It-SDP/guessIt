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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.MainMenuActivity.Companion.PLAY
import com.github.freeman.bootcamp.MainMenuActivity.Companion.PROFILE
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SETTINGS
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    MainMenuScreen()
}