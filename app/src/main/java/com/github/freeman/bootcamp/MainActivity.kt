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
    final val AUTH_REQUEST_code = 7192
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var listener: FirebaseAuth.AuthStateListener
    lateinit var providers: List<AuthUI.IdpConfig>

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listener)
        init()

    }

    override fun onStop() {
        if (listener != null) {
            firebaseAuth.addAuthStateListener(listener);
        }
        super.onStop()
    }

    private fun init() {
        providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        firebaseAuth = FirebaseAuth.getInstance()
        listener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user = p0.currentUser
                if (user != null)// Already Login
                {
                    //Do something
                    Toast.makeText(this@MainActivity, "" + user.uid, Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build()
                    )
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun greet(view: View) {
        val myIntent = Intent(this, GreetingActivity::class.java)
        val name = findViewById<EditText>(R.id.mainName).text.toString()
        myIntent.putExtra("name", name) //Optional parameters
        startActivity(myIntent)
    }
}