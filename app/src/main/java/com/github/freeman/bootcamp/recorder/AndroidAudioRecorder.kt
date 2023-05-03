package com.github.freeman.bootcamp.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.provider.MediaStore.Audio.Media
import androidx.core.net.toUri
import com.github.freeman.bootcamp.utilities.firebase.References
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(
    private val context: Context
):DistantAudioRecorder {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

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

    override fun stop(audioFile: File, id : String) {
        recorder?.stop()
        recorder?.reset()
        recorder = null
        if(audioFile!= null) {
            References.voiceNotesStorageRef.child(id).putFile(audioFile!!.toUri())
        }
    }
}