package com.github.freeman.bootcamp.recorder

import java.io.File

interface DistantAudioPlayer {
    fun playFile(file: File, id: String)
    fun stop()
}