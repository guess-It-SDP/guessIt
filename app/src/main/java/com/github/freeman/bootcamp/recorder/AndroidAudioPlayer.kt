package com.github.freeman.bootcamp.recorder

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import com.github.freeman.bootcamp.utilities.firebase.References
import java.io.File

class AndroidAudioPlayer(
    private val context: Context
) : DistantAudioPlayer {

    private var player: MediaPlayer? = null

    fun playFile(file: File) {


        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    override fun playFile(file: File, id: String) {
        References.voiceNotesStorageRef.child(id).getFile(file!!.toUri()).addOnSuccessListener {
            this.playFile(file!!)
        }
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }
}