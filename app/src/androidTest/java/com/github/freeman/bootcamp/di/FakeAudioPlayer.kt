package com.github.freeman.bootcamp.di

import com.github.freeman.bootcamp.recorder.DistantAudioPlayer
import java.io.File

class FakeAudioPlayer:DistantAudioPlayer {
    override fun playFile(file: File, id: String) {

    }

    override fun stop() {

    }
}