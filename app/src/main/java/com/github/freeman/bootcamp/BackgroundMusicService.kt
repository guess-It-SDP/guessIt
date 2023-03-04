package com.github.freeman.bootcamp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BackgroundMusicService : Service() {

    private val tag = "BackgroundMusicServiceLog"
    private lateinit var mediaPlayer: MediaPlayer
//    private lateinit var binder: MusicBinder

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
//        return binder;
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(tag, "Executing onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(tag, "Executing onStartCommand")
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.waterfall)
        mediaPlayer.isLooping = true;
        mediaPlayer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "Executing onDestroy")
        mediaPlayer.stop()
    }

//    inner class MusicBinder : Binder() {
//        val service: BackgroundMusicService
//            get() = this@BackgroundMusicService
//    }
}