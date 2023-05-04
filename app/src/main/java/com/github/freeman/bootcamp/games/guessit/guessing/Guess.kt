package com.github.freeman.bootcamp.games.guessit.guessing

/**
 * Represents a message typed in the guessing screen
 */
data class Guess(
    val guesser: String? = null,
    val guesserId: String? = null,
    val message: String? = null
)