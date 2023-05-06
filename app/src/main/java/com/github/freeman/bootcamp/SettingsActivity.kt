package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.SettingsActivity.Companion.MUSIC_VOLUME
import com.github.freeman.bootcamp.SettingsActivity.Companion.PARAMETERS_TITLE
import com.github.freeman.bootcamp.SettingsActivity.Companion.SETTINGS_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                SettingsScreen()
            }
        }
    }

    companion object {
        const val PARAMETERS_TITLE = "App Parameters"
        const val SETTINGS_TITLE = "Settings"
        const val MUSIC_VOLUME = "Music Volume"
    }
}

fun back(context: Context) {
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
            val context = LocalContext.current
            TopAppBar(
                modifier = Modifier.testTag("topAppbarSettings"),
                title = {
                    androidx.compose.material.Text(
                        text = PARAMETERS_TITLE,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                backgroundColor = androidx.compose.material.MaterialTheme.colors.background,
                elevation = 4.dp,
                navigationIcon = {
                    androidx.compose.material.IconButton(
                        onClick = {
                            val activity = (context as? Activity)
                            activity?.finish()
                        },
                        modifier = Modifier.testTag("settingsBackButton")
                    ) {
                        androidx.compose.material.Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
                }
            )
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