package com.github.freeman.bootcamp.utilities.firebase

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/**
 *contains the references to the audio files int the storage of the Firebase Database.
 */
object References {
    val voiceNotesStorageRef = Firebase.storage.reference.child("Audio")
}