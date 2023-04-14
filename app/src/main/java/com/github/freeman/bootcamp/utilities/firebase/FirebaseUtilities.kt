package com.github.freeman.bootcamp.utilities.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import java.util.concurrent.CompletableFuture

/**
 * Utility functions related to Firebase
 */
object FirebaseUtilities {

    /**
     * Gets the value located in the given database reference
     * @param dbRef database reference
     * @return a future of the value of type String contained in the database
     */
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

    /**
     * Gets the value located in the given database reference
     * @param dbRef database reference
     * @return a future of the value of type Long contained in the database
     */
    fun databaseGetLong(dbRef: DatabaseReference): CompletableFuture<Long> {
        val future = CompletableFuture<Long>()
        dbRef.get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as Long)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    /**
     * Gets the value located in the given database reference
     * @param dbref database reference
     * @return a future of the value of type Map<String, Int> contained in the database
     */
    fun databaseGetMap(dbRef: DatabaseReference): CompletableFuture<Map<*, *>> {
        val future = CompletableFuture<Map<*, *>>()
        dbRef.get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as Map<*, *>?)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        return future
    }

    /**
     * Gets the value located in the given storage reference
     * @param storageRef storage reference
     * @return a future of the file contained in the storage, in a bitmap format
     */
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

    /**
     * Checks if a Guess It profile exists in the database
     * @param user the user liked to the profile
     * @param dbRef database reference
     * @return a future containing the boolean
     */
    fun profileExists(user: FirebaseUser?, dbRef: DatabaseReference): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        if (user == null) {
            future.complete(false)
        } else {
            // if the email exists, the profile exists too
            databaseGet(dbRef.child("profiles/${user.uid}/email"))
                .thenAccept {
                    future.complete(it != "")
                }
        }

        return future
    }


}