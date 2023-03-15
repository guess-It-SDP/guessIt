package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
                SettingsScreen()
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
fun SettingsBackButton() {
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
            Spacer(modifier = Modifier.size(50.dp))
            Text(text = MUSIC_VOLUME)
            Spacer(modifier = Modifier.size(10.dp))
            MusicSlider()
        }
    }
}

@Composable
private fun MusicSlider() {
//    val backgroundMusicService = BackgroundMusicService.BGMService
//    while(backgroundMusicService == null) {
//        val backgroundMusicService = BackgroundMusicService.BGMService
//    }
//    var backgroundMusicService: BackgroundMusicService
//    if (BackgroundMusicService.isRunning) {
//        backgroundMusicService = BackgroundMusicService.BGMService
//    } else {
//        val i = Intent(LocalContext.current, BackgroundMusicService::class.java)
//        LocalContext.current.startService(i)
//        backgroundMusicService = BackgroundMusicService.BGMService
//    }

//    val backgroundMusicService = BackgroundMusicService().BGMService2

//    var sliderValue by remember {
//        mutableStateOf(backgroundMusicService?.getCurrentVolume()) // pass the initial value
//    }


//    val backgroundMusicService = BackgroundMusicService().getInstance()
//    while(backgroundMusicService == null) {
//        val backgroundMusicService = BackgroundMusicService().getInstance()
//    }


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

//    sliderValue?.let {
//        Slider(
//        value = it,
//        onValueChange = { sliderValue_ ->
//            sliderValue = sliderValue_
//        },
//        modifier = Modifier.padding(50.dp),
//        valueRange = 0.01f..1f
//    )
//    }
//
//    sliderValue?.let { backgroundMusicService?.changeVolume(it, sliderValue!!) }
//    sliderValue?.let { backgroundMusicService?.saveVolume(it) }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}