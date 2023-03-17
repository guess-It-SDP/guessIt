package com.github.freeman.bootcamp.recorder

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}