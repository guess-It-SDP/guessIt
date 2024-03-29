package com.github.freeman.bootcamp

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
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
        isRunning = true
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
        isRunning = false
    }

    companion object {
        lateinit var BGMService: BackgroundMusicService
        var isRunning = false
        const val ACTION_STOP_BACKGROUND_MUSIC = "com.example.app.STOP_BACKGROUND_MUSIC"
    }

    private val stopBackgroundMusicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_STOP_BACKGROUND_MUSIC) {
                // Stop the background music service
                stopSelf()
            }
        }
    }

    fun changeVolume(leftVolume: Float, rightVolume: Float) {
        mediaPlayer.setVolume(leftVolume, rightVolume)
    }

    fun saveVolume(volume: Float) {
        currentVolume = volume
    }

    fun getCurrentVolume(): Float {
        return currentVolume
    }
}