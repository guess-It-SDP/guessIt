package com.github.freeman.bootcamp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture


class FireApp  : AppCompatActivity(){
    lateinit var db0: FirebaseDatabase
    lateinit var db: DatabaseReference
    val future = CompletableFuture<String>()
    var EMAIL: String = ""
    var PHONE_NB: String = ""
    lateinit var phone : EditText
    lateinit var emailView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fire_app)
        phone = findViewById<EditText>(R.id.phone)
        emailView = findViewById<EditText>(R.id.email)
        db0 = Firebase.database("https://sdp-firebase-bootcamp-89ce8-default-rtdb.europe-west1.firebasedatabase.app/")
        db = db0.reference
    }

    fun get(view: View) {
        updateEmailAndPhone()
        db.child(PHONE_NB).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        future.thenAccept {
            emailView.setText(it.toString())
        }
    }

    fun set(view: View) {
        updateEmailAndPhone()

        db.child(PHONE_NB).setValue(EMAIL)

    }

    fun updateEmailAndPhone() {
        EMAIL = emailView.text.toString()
        PHONE_NB = phone.text.toString()
    }
}