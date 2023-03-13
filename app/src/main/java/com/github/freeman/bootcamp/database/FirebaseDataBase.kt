package com.github.freeman.bootcamp.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

class FirebaseDataBase: Database {
    private val db: DatabaseReference = Firebase.database.reference

    override fun set(key: String, value: String) {
        db.child(key).setValue(value)
    }

    override fun get(key: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        db.child(key).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }
}