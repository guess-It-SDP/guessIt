package com.github.freeman.bootcamp.recorder

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import com.github.freeman.bootcamp.utilities.firebase.References
import java.io.File

/**
 * Gets an audio file from the database and play it.
 */
class AndroidAudioPlayer(
    private val context: Context
) : DistantAudioPlayer {

    private var player: MediaPlayer? = null

    /**
     * Plays the music cached
     * @param file cache where the audio content is located
     */
    fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    /**
     * Play a music or an audio file downloaded from the Database.
     * @param file a file to cache the audio content
     * @param id the id of the file located in the position referred in the database
     */
    override fun playFile(file: File?, id: String) {
        if(file != null) {
            References.voiceNotesStorageRef.child(id).getFile(file!!.toUri()).addOnSuccessListener {
                this.playFile(file!!)

            }
        }
    }

    /**
     * Stop the file that is currently being played
     */
    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }
}