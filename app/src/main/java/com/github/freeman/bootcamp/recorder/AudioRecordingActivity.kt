package com.github.freeman.bootcamp.recorder

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.core.app.ActivityCompat
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import java.io.File

class AudioRecordingActivity : ComponentActivity() {

    companion object {
        const val AUDIO_FILE = "audio.mp3"
        const val START_RECORDING_BUTTON = "Start recording"
        const val STOP_RECORDING_BUTTON = "Stop recording"
        const val PLAY_BUTTON = "Play"
        const val STOP_PLAYING = "Stop playing"
    }

    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        setContent {
            BootcampComposeTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        File(cacheDir, AUDIO_FILE).also {
                            recorder.start(it)
                            audioFile = it
                        }
                    },modifier = Modifier.testTag("start_recording_button")) {
                        Text(text = START_RECORDING_BUTTON)
                    }
                    Button(onClick = {
                        recorder.stop()
                    },modifier = Modifier.testTag("stop_recording_button")) {
                        Text(text = STOP_RECORDING_BUTTON)
                    }
                    Button(onClick = {
                        player.playFile(audioFile ?: return@Button)
                    },modifier = Modifier.testTag("play_button")) {
                        Text(text = PLAY_BUTTON)
                    }
                    Button(onClick = {
                        player.stop()
                    },modifier = Modifier.testTag("stop_button")) {
                        Text(text = STOP_PLAYING)
                    }
                }
            }
        }
    }
}
