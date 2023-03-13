package com.github.freeman.bootcamp.record


import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.core.net.toUri
import java.io.File

class AndroidAudioPlayer(
    private val context: Context
): AudioPlayer {

    private var player: MediaPlayer? = null

    override fun playFile(file: File) {
        Log.d("HI","hi")
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }

    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }
}