package com.github.freeman.bootcamp

import android.content.Intent
import android.os.Bundle
import android.view.ActionProvider
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun greet(view: View) {
        val myIntent = Intent(this, GreetingActivity::class.java)
        val name = findViewById<EditText>(R.id.mainName).text.toString()
        myIntent.putExtra("name", name) //Optional parameters
        startActivity(myIntent)
    }
}