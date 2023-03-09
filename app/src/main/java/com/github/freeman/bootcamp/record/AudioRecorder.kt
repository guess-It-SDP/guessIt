package com.github.freeman.bootcamp.record

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}