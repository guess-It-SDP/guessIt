package com.github.freeman.bootcamp.utilities.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.github.freeman.bootcamp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
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
            else if (it.value is Long) {
                future.complete((it.value as Long).toString())
            } else if (it.value is Double) {
                future.complete((it.value as Double).toString())
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
     * @return a future of the value of type Double contained in the database
     */
    fun databaseGetDouble(dbRef: DatabaseReference): CompletableFuture<Double> {
        val future = CompletableFuture<Double>()
        dbRef.get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as Double)
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
     * @param userId the user linked to the profile
     * @param dbRef database reference
     * @return a future containing the boolean
     */
    fun profileExists(userId: String, dbRef: DatabaseReference): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        databaseGet(dbRef.child("profiles/$userId/username"))
            .thenAccept {
                future.complete(it != "" && it != null)
            }

        return future
    }

    /**
     * Creates a profile and stores it into Firebase
     *
     * @param context context of the activity
     * @param userId Id of the user
     * @param username desired username
     * @param email desired email
     */
    fun createProfile(context: Context, userId: String, username: String, email: String? = null) {
        val dbRef = Firebase.database.reference
        val storageRef = Firebase.storage.reference

        // username + email
        dbRef
            .child(context.getString(R.string.profiles_path))
            .child(userId)
            .child(context.getString(R.string.username_path))
            .setValue(username)
        if (email != null) {
            dbRef
                .child(context.getString(R.string.profiles_path))
                .child(userId)
                .child(context.getString(R.string.email_path))
                .setValue(email)
        }

        // default profile picture
        val picBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.raw.default_profile_pic
        )

        val stream = ByteArrayOutputStream()
        picBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val image = stream.toByteArray()
        storageRef
            .child(context.getString(R.string.profiles_path))
            .child(userId)
            .child(context.getString(R.string.picture_path))
            .putBytes(image)
    }

    /**
     * Get the DatabaseReference of a gameId ("testgameid" by default)
     * @param context the current local context
     * @param gameId the game id of which we want the reference
     */
    fun getGameDBRef(context: Context, gameId: String = context.getString(R.string.test_game_id)): DatabaseReference {
        val dbRef = Firebase.database.reference
            .child(context.getString(R.string.games_path))
            .child(gameId)
        return dbRef
    }


}