package com.github.freeman.bootcamp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class BackgroundMusicService : Service() {

    private val tag = "BackgroundMusicServiceLog"
    private lateinit var mediaPlayer: MediaPlayer
    private var currentVolume = 1f

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(tag, "Executing onCreate")
        BGMService = this
//        mediaPlayer = MediaPlayer.create(this.applicationContext, R.raw.waterfall)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(tag, "Executing onStartCommand")
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.waterfall)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "Executing onDestroy")
        mediaPlayer.stop()
    }

    companion object {
        lateinit var BGMService: BackgroundMusicService
    }

    fun changeVolume(leftVolume: Float, rightVolume: Float) {
        // Removing the else loop stops the app from crashing but still no sound is heard
//        if (::mediaPlayer.isInitialized) {
        mediaPlayer.setVolume(leftVolume, rightVolume)
//        }
//        else {
//            onCreate()
//            mediaPlayer.setVolume(leftVolume, rightVolume)
//        }
    }

    fun saveVolume(volume: Float) {
        currentVolume = volume
    }

    fun getCurrentVolume(): Float {
        return currentVolume
    }
}