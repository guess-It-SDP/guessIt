package com.github.freeman.bootcamp.utilities.firebase

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/**
 * Contains the references to the audio file folder in the storage of the Firebase Database.
 */
object References {
    val AUDIO = "audio"

    val voiceNotesStorageRef = Firebase.storage.reference.child(AUDIO)
}