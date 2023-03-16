package com.github.freeman.bootcamp


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
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

/**
 * This class is for demonstrating and for debugging
 * The MainMenuActivity will be the main screen of the app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                MainScreen()
            }
        }
    }
    @Composable
    fun MainScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("mainMenuScreen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.testTag("gameName"),
                text = "Guess It!",
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.size(50.dp))
            ChatButton()
            Spacer(modifier = Modifier.size(8.dp))
            DrawingActivity()
            Spacer(modifier = Modifier.size(8.dp))
            GuessingActivity()
            Spacer(modifier = Modifier.size(8.dp))
            MainMenuActivity()
        }
    }

    @Composable
    fun ChatButton() {
        ElevatedButton(
            modifier = Modifier.testTag("chat_activity_button"),
            onClick = { startActivity(Intent(this, ChatActivity::class.java)) }
        ) {
            Text("Chat")
        }
    }

    @Composable
    fun DrawingActivity() {
        ElevatedButton(
            modifier = Modifier.testTag("drawing_activity_button"),
            onClick = { startActivity(Intent(this, DrawingActivity::class.java)) }
        ) {
            Text("Draw")
        }
    }

    @Composable
    fun GuessingActivity() {
        ElevatedButton(
            modifier = Modifier.testTag("guessing_activity_button"),
            onClick = { startActivity(Intent(this, GuessingActivity::class.java)) }
        ) {
            Text("Guess")
        }
    }

    @Composable
    fun MainMenuActivity() {
        ElevatedButton(
            modifier = Modifier.testTag("main_menu_activity_button"),
            onClick = { startActivity(Intent(this, MainMenuActivity::class.java)) }
        ) {
            Text("Main menu and music")
        }
    }

}

