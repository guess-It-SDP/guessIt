package com.github.freeman.bootcamp.utilities.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import java.util.concurrent.CompletableFuture
import com.github.freeman.bootcamp.R

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
            else if (it.value is Long) {
                future.complete((it.value as Long).toString())
            } else {
                future.complete(it.value as String)
            }

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
     * @param dbRef database reference
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
     * Gets the value located in the given database reference
     * @param dbRef database reference
     * @return a future of the value of type List contained in the database
     */
    fun databaseGetList(dbRef: DatabaseReference): CompletableFuture<List<*>> {
        val future = CompletableFuture<List<*>>()
        dbRef.get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as List<*>?)
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
    fun profileExists(context: Context, user: FirebaseUser?, dbRef: DatabaseReference): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        if (user == null) {
            future.complete(false)
        } else {
            // if the email exists, the profile exists too
            databaseGet(dbRef.child(context.getString(R.string.profiles_path))
                .child(user.uid)
                .child(context.getString(R.string.username_path)))
                .thenAccept {
                    future.complete(it != "")
                }
        }

        return future
    }


}