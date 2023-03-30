package com.github.freeman.bootcamp.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import java.util.concurrent.CompletableFuture

object FirebaseUtilities {

    fun databaseGet(dbRef: DatabaseReference): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        dbRef.get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    fun storageGet(storageRef: StorageReference): CompletableFuture<Bitmap?> {
        val future = CompletableFuture<Bitmap?>()
        val ONE_MEGABYTE: Long = 1024 * 1024
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            future.complete(BitmapFactory.decodeByteArray(it, 0, it.size))
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future

    }


}