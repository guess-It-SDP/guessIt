package com.github.freeman.bootcamp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BackgroundMusicExampleActivity : AppCompatActivity() {

    private lateinit var i: Intent
//    private var isRunning = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_music_example)
        val startService = findViewById<Button>(R.id.buttonStartService)
        val stopService = findViewById<Button>(R.id.buttonStopService)
//        val conn = BackgroundMusicServiceConnection()

        startService.setOnClickListener {
            i = Intent(this, BackgroundMusicService::class.java)
//            bindService(i, conn, Context.BIND_AUTO_CREATE)
            startService(i)
        }

        stopService.setOnClickListener {
            stopService(i)
//            unbindService(conn)
//            isRunning = false
        }
    }

//    inner class BackgroundMusicServiceConnection : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder: BackgroundMusicService.MusicBinder = service as BackgroundMusicService.MusicBinder
//            backgroundMusicService = binder.service
//            isRunning = true
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            isRunning = false
//        }
//
//    }

    fun back(view: View) {
        this.finish()
    }
}