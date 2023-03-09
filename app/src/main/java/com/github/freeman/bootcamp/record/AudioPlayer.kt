package com.github.freeman.bootcamp.record

import android.media.MediaPlayer
import androidx.compose.material3.MediumTopAppBar
import java.io.File



interface AudioPlayer {
   fun playFile(file:File)
   fun stop()
}