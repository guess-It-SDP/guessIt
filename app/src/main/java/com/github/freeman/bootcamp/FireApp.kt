package com.github.freeman.bootcamp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.CompletableFuture


class FireApp : AppCompatActivity() {
    lateinit var db0: FirebaseDatabase
    lateinit var db: GenericDatabase
    val future = CompletableFuture<String>()
    var EMAIL: String = ""
    var PHONE_NB: String = ""
    lateinit var phone: EditText
    lateinit var emailView: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fire_app)
        phone = findViewById<EditText>(R.id.phone)
        emailView = findViewById<EditText>(R.id.email)
        db = GenericFirebaseDatabase()
    }

    fun get(view: View) {
        updateEmailAndPhone()
        emailView.setText(db.get(EMAIL))
    }
    fun set(view: View) {
        updateEmailAndPhone()
        db.set(PHONE_NB,EMAIL)
    }

    fun updateEmailAndPhone() {
        EMAIL = emailView.text.toString()
        PHONE_NB = phone.text.toString()
    }

    fun setDB(gd: GenericDatabase){
        db = gd
    }
}