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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths



class AudioRecordingActivity : ComponentActivity() {

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


    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null
    private var audioFile2: File?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )

        val storageRef = Firebase.storage.reference
        val voiceNoteRef = storageRef.child("Audio/voiceNote")
        setContent {
            val context = LocalContext.current
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
                        if(audioFile!= null) {
                            voiceNoteRef.putFile(audioFile!!.toUri())
                        }
                    },modifier = Modifier.testTag("stop_recording_button")) {
                        Text(text = STOP_RECORDING_BUTTON)
                    }
                    Button(onClick = {
                        audioFile2 =  File(cacheDir, AUDIO_FILE2)
                        voiceNoteRef.getFile(audioFile2!!.toUri()).addOnSuccessListener {  player.playFile(audioFile2!!)
                        }


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
