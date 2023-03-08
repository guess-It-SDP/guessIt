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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_music_example)
        val startService = findViewById<Button>(R.id.buttonStartService)
        val stopService = findViewById<Button>(R.id.buttonStopService)

        startService.setOnClickListener {
            i = Intent(this, BackgroundMusicService::class.java)
            startService(i)
        }

        stopService.setOnClickListener {
            stopService(i)
        }
    }

    fun back(view: View) {
        this.finish()
    }
}