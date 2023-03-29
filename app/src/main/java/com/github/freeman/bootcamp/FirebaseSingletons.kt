package com.github.freeman.bootcamp

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseSingletons {
    val database = Singleton(Firebase.database.reference)
}