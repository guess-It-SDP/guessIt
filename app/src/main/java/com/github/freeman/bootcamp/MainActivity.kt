package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.android.material.R

class MainActivity : ComponentActivity() {
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
}

// this class is to store the value of the text field
// in order to use it in other Composable
class TextFieldState{
    var text: String by mutableStateOf("")
}

fun greet(context: Context, name: String) {
    if (!name.isEmpty()) {
        context.startActivity(Intent(context, GreetingActivity::class.java).apply {
            putExtra("name", name)
        })
    }
}

@Composable
fun GreetingInput(msg : TextFieldState = remember { TextFieldState() }) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    OutlinedTextField(
        modifier = Modifier.testTag("greetingInput"),
        value = text,
        label = {
            Text(text = "Enter Your Name")
        },
        onValueChange = {
            text = it
            msg.text = it.text
        }
    )
}

@Composable
fun GreetingButton(msg : TextFieldState = remember { TextFieldState() }) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("greetingButton"),
        onClick = {
            greet(context, msg.text)
        }
    ) {
        Text("Greet me!")
    }
}

@Composable
fun SettingsButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("settingsButton"),
        onClick = {
            settings(context)
        }
    ) {
        Text(SettingsActivity.SETTINGS_TITLE)
    }
}

fun settings(context: Context) {
    context.startActivity(Intent(context, SettingsActivity::class.java))
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mainScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textState = remember { TextFieldState() }
        GreetingInput(textState)
        GreetingButton(textState)
        SettingsButton()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}

@Preview
@Composable
fun GreetingButtonPreview() {
    GreetingButton()
}

@Preview
@Composable
fun GreetingInputPreview() {
    GreetingInput()
}

@Preview
@Composable
fun SettingsButtonPreview() {
    SettingsButton()
}