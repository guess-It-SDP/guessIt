package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
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
}

fun play(context: Context) {
    //context.startActivity(Intent(context, PlayMenuActivity::class.java))
}

@Composable
fun PlayButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("playButton"),
        onClick = { play(context) }
    ) {
        Text("Play")
    }
}

fun settings(context: Context) {
    //context.startActivity(Intent(context, SettingsActivity::class.java))
}

@Composable
fun SettingsButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("settingsButton"),
        onClick = { play(context) }
    ) {
        Text("Settings")
    }
}

fun profile(context: Context) {
    //context.startActivity(Intent(context, ProfileActivity::class.java))
}

@Composable
fun ProfileButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("profileButton"),
        onClick = { play(context) }
    ) {
        Text("Profile")
    }
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
        PlayButton()
        SettingsButton()
        ProfileButton()
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    MainScreen()
}

@Preview
@Composable
fun PlayButtonPreview() {
    GreetingButton()
}