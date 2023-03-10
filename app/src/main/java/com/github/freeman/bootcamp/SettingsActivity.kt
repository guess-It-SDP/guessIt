package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.SettingsActivity.Companion.BACK
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                Display()
            }
        }
    }

    companion object {
        const val SETTINGS_TITLE = "Settings"
        const val BACK = "Back"
    }
}

@Composable
fun Settings(){
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("settings"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("This is the settings menu! (work in progress)")
        }
    }
}


@Composable
fun BackButton() {
    val context = LocalContext.current
    Button(
        modifier = Modifier.testTag("backButton"),
        onClick = {
            back(context)
        }
    ) {
        Text(BACK)
    }
}

fun back(context: Context) {
    val activity = (context as? Activity)
    activity?.finish()
}

@Composable
fun Display() {
    MaterialTheme() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("displaySettings1"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Settings()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("displaySettings2"),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            BackButton()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayPreview() {
    Display()
}