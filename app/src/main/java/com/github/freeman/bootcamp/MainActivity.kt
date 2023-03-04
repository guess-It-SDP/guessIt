package com.github.freeman.bootcamp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val i = Intent(this, BackgroundMusicService::class.java)
        startService(i)
    }

    fun greet(view: View) {
        val myIntent = Intent(this, GreetingActivity::class.java)
        val name = findViewById<EditText>(R.id.mainName).text.toString()
        myIntent.putExtra("name", name) //Optional parameters
        startActivity(myIntent)
    }

    fun testBackgroundMusic(view: View) {
        val myIntent = Intent(this, BackgroundMusicExampleActivity::class.java)
        startActivity(myIntent)
    }

}