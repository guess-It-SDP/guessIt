package com.github.freeman.bootcamp.di

import android.content.Context
import com.github.freeman.bootcamp.recorder.AndroidAudioPlayer
import com.github.freeman.bootcamp.recorder.AndroidAudioRecorder
import com.github.freeman.bootcamp.recorder.DistantAudioPlayer
import com.github.freeman.bootcamp.recorder.DistantAudioRecorder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Inject

class AudioRecorderImpl @Inject constructor(context: Context)
    :DistantAudioRecorder{
    val myRecoder = AndroidAudioRecorder(context);
    override fun start(outputFile: File) {
        myRecoder.start(outputFile)
    }
    override fun stop(audioFile: File, id: String) {
       myRecoder.stop(audioFile,id)
    }
}

class AudioPlayerImpl @Inject constructor(context: Context)
    :DistantAudioPlayer{
    val myPlayer = AndroidAudioPlayer(context);
    override fun playFile(file: File?, id: String) {
     file ?:   myPlayer.playFile(file,id)
    }
    override fun stop() {
        myPlayer.stop()
    }
}




@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    fun provideAudioRecorder(@ApplicationContext context: Context):DistantAudioRecorder = AudioRecorderImpl(context)

    @Provides
    fun provideAudioPlayer(@ApplicationContext context: Context):DistantAudioPlayer = AudioPlayerImpl(context)
}