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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.github.freeman.bootcamp.di.AppModule
import com.github.freeman.bootcamp.di.AppModule_ProvideAudioPlayerFactory.provideAudioPlayer
import com.github.freeman.bootcamp.di.AppModule_ProvideAudioRecorderFactory.provideAudioRecorder
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.References
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.inject.Inject


/**
 * First requests the user for permission to record audio
 * Consists of four buttons: "Start recording", "Stop recording", "Play", and "Stop playing".
 * When the "Start recording" button is clicked, the recorder property starts recording audio
 * to a file in cache. When the "Stop recording" button is clicked, the recorder stops
 * recording and saves the recorded audio file to Firebase storage. When the "Play" button is
 * clicked, the audio file is downloaded from Firebase storage and played using the player property.
 * Finally, when the "Stop playing" button is clicked, the player stops playing the audio file.
 */
@AndroidEntryPoint
class AudioRecordingActivity : ComponentActivity() {
    var id: String? = null

    fun downloadFile(url: URL, fileName: String) {
        url.openStream().use { Files.copy(it, Paths.get(fileName)) }
    }

    companion object {
        const val AUDIO_FILE = "audio.mp3"
        const val AUDIO_FILE2 = "audio2.mp3"
        const val START_RECORDING_BUTTON = "Start recording"
        const val STOP_RECORDING_BUTTON = "Stop recording"
        const val PLAY_BUTTON = "Play"
        const val STOP_PLAYING = "Stop playing"
    }


    /*
    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }
    */


    private var audioFile: File? = null
    private var audioFile2: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.RECORD_AUDIO), 0
        )

        val storageRef = Firebase.storage.reference
        val voiceNoteRef = storageRef.child("Audio/voiceNote")
        setContent {
            val context = LocalContext.current
            var recorder :DistantAudioRecorder = provideAudioRecorder(AppModule(), LocalContext.current)
            var player :DistantAudioPlayer = provideAudioPlayer(AppModule(), LocalContext.current)
            BootcampComposeTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        id = UUID.randomUUID().toString()
                        File(cacheDir, AUDIO_FILE).also {
                            recorder.start(it)
                            audioFile = it
                        }


                    }, modifier = Modifier.testTag("start_recording_button")) {
                        Text(text = START_RECORDING_BUTTON)
                    }
                    Button(onClick = {
                        recorder.stop(audioFile!!, id!!)
                    }, modifier = Modifier.testTag("stop_recording_button")) {
                        Text(text = STOP_RECORDING_BUTTON)
                    }
                    Button(onClick = {
                        audioFile2 = File(cacheDir, AUDIO_FILE2)
                        player.playFile(audioFile2!!, id!!)


                    }, modifier = Modifier.testTag("play_button")) {
                        Text(text = PLAY_BUTTON)
                    }
                    Button(onClick = {
                        player.stop()
                    }, modifier = Modifier.testTag("stop_button")) {
                        Text(text = STOP_PLAYING)
                    }
                }
            }
        }
    }
}
