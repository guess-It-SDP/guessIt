package com.github.freeman.bootcamp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class GreetingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)
        val name = intent.getStringExtra("name")
        findViewById<TextView>(R.id.greetingMessage).text = getString(R.string.greeting_message, name)

    }
}