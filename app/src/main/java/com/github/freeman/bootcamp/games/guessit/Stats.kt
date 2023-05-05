package com.github.freeman.bootcamp.games.guessit

data class Stats (
    val nb_games_played: Int = 0,
    val best_score: Int = 0,
    val nb_correct_guesses: Int = 0,
    val percentage_first_guesser: Double = 0.0,
    val percentage_guesser: Double = 0.0,
    val percentage_somebody_guessed: Double = 0.0
)