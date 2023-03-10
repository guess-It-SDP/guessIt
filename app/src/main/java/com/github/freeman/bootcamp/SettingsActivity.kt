package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.SettingsActivity.Companion.BACK
import com.github.freeman.bootcamp.SettingsActivity.Companion.MUSIC_VOLUME
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
        const val MUSIC_VOLUME = "Music Volume"
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
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("displaySettings1"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = MUSIC_VOLUME)
            MusicSlider()
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

@Composable
private fun MusicSlider() {
    val backgroundMusicService = BackgroundMusicService.BGMService
    var sliderValue by remember {
        mutableStateOf(backgroundMusicService.getCurrentVolume()) // pass the initial value
    }

    Slider(
        value = sliderValue,
        onValueChange = { sliderValue_ ->
            sliderValue = sliderValue_
        },
        modifier = Modifier.padding(50.dp),
        valueRange = 0.01f..1f
    )

    backgroundMusicService.changeVolume(sliderValue, sliderValue)
    backgroundMusicService.saveVolume(sliderValue)
}

@Preview(showBackground = true)
@Composable
fun DisplayPreview() {
    Display()
}