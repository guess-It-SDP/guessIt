package com.github.freeman.bootcamp

import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object FirebaseEmulator {
    fun init() {
        val databaseInstance = FirebaseDatabase.getInstance("http://10.0.2.2:9000/?ns=sdp-guess-it-default-rtdb").reference
        val storageInstance = FirebaseStorage.getInstance("gs://sdp-guess-it.appspot.com/").reference

        FirebaseSingletons.database.set(databaseInstance)
        FirebaseSingletons.storage.set(storageInstance)

    }
}