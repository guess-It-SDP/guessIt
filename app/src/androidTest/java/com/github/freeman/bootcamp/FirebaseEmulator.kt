package com.github.freeman.bootcamp

import com.google.firebase.database.FirebaseDatabase

object FirebaseEmulator {
    fun init() {
        val databaseInstance = FirebaseDatabase.getInstance("http://10.0.2.2:9000/?ns=sdp-guess-it-default-rtdb").reference
        FirebaseSingletons.database.set(databaseInstance)

    }
}