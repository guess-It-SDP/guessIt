package com.github.freeman.bootcamp.di

import com.github.freeman.bootcamp.recorder.DistantAudioRecorder
import java.io.File

class FakeAudioRecorder: DistantAudioRecorder {
    override fun start(outputFile: File) {

    }

    override fun stop(audioFile: File, id: String) {

    }
}