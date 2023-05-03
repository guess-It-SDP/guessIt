package com.github.freeman.bootcamp.recorder

import java.io.File

/**
 * Cache audio from the mic and sends it to the Database.
 */
interface DistantAudioRecorder {
    /**
     * Cache an audio file recorded from the mic
     * @param outputFile the file used to store the audio
     */
    fun start(outputFile: File)

    /**
     * Select the cached audio file and sends it to the database to the reffered id.
     * @param audioFile the file where the audio is chached
     * @param id the id where the file will be refered in the storage Database
     */
    fun stop(audioFile: File, id : String)
}