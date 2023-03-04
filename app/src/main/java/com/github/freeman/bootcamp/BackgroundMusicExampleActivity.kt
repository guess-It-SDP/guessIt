package com.github.freeman.bootcamp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BackgroundMusicExampleActivity : AppCompatActivity() {

//    val startService: Button
//    val stopService: Button
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


}