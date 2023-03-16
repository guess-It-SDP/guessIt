package com.github.freeman.bootcamp


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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.record.AudioRecordingActivity

import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
    }

@Composable
fun AudioRecorderButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("AudioRecorderButton"),
        onClick = {
            context.startActivity(Intent(context, AudioRecordingActivity::class.java))
        }
    ) {
        Text("Login")
    }
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
        AudioRecorderButton()
    }
}

}