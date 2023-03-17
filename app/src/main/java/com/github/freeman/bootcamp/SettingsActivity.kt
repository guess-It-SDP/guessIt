package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.SettingsActivity.Companion.MUSIC_VOLUME
import com.github.freeman.bootcamp.SettingsActivity.Companion.SETTINGS_TITLE
import com.github.freeman.bootcamp.SettingsActivity.Companion.isRunning
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRunning = true
        setContent {
            BootcampComposeTheme {
                SettingsScreen()
            }
        }
    }

    companion object {
        const val SETTINGS_TITLE = "Settings"
        const val MUSIC_VOLUME = "Music Volume"
        var isRunning = false
    }
}

@Composable
fun SettingsBackButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("settingsBackButton"),
        onClick = {
            back(context)
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back arrow icon"
        )
    }
}

fun back(context: Context) {
    isRunning = false
    val activity = (context as? Activity)
    activity?.finish()
}

@Composable
fun SettingsScreen() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            SettingsBackButton()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("settingsScreen"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.testTag(SETTINGS_TITLE),
                text = SETTINGS_TITLE,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.size(150.dp))
            Text(text = MUSIC_VOLUME,
                 modifier = Modifier.testTag(MUSIC_VOLUME))
            MusicSlider()
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
