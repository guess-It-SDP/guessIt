package com.github.freeman.bootcamp.recorder

import java.io.File

/**
 * Gets an audio file from the database and play it.
 */
interface DistantAudioPlayer {
    /**
     * Play a music or an audio file downloaded from the Database.
     * @param file a file to cache the audio content
     * @param id the id of the file located in the position referred in the database
     */
    fun playFile(file: File?, id: String)

    /**
     * Stop the file that is currently being played
     */
    fun stop()
}