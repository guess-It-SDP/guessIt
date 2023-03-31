package com.github.freeman.bootcamp.firebase

import com.github.freeman.bootcamp.Singleton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseSingletons {
    val database = Singleton(Firebase.database.reference)
    val storage = Singleton(Firebase.storage.reference)
}