package com.github.freeman.bootcamp.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.core.net.toUri
import com.github.freeman.bootcamp.utilities.firebase.References
import java.io.File
import java.io.FileOutputStream

/**
 * Cache audio from the mic and sends it to the Database.
 */
class AndroidAudioRecorder(
    private val context: Context
):DistantAudioRecorder {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }


    /**
     * Cache an audio file recorded from the mic
     * @param outputFile the file used to store the audio
     */
    override fun start(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            //setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    /**
     * Select the cached audio file and sends it to the database to the referred id.
     * @param audioFile the file where the audio is cached
     * @param id the id where the file will be referred in the storage Database
     */
    override fun stop(audioFile: File, id : String) {
        recorder?.stop()
        recorder?.reset()
        recorder = null
        References.voiceNotesStorageRef.child(id).putFile(audioFile.toUri())
    }
}