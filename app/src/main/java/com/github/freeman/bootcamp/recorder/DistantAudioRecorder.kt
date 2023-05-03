package com.github.freeman.bootcamp.recorder

import java.io.File

interface DistantAudioRecorder {
    fun start(outputFile: File)
    fun stop(audioFile: File, id : String)
}