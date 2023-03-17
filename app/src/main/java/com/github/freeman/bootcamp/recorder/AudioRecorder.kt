package com.github.freeman.bootcamp.recorder

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}