package com.github.freeman.bootcamp

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture


class GenericFirebaseDatabase : GenericDatabase {
    private val db = Firebase.database("https://sdp-firebase-bootcamp-89ce8-default-rtdb.europe-west1.firebasedatabase.app/").reference

    override fun set(key: String, value: String) {
        db.child(key).setValue(value)
    }

    override fun get(key: String): String {
        val future = CompletableFuture<String>()

        db.child(key).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        future.thenAccept {
            it.toString()
        }
        return future.join()
    }
}