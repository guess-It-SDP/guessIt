package com.github.freeman.bootcamp.games.guessit

/**
 * Represents the structure of a game/lobby in the database
 */
data class GameData (
    val Current: Current,
    val Parameters: Parameters,
    val Players: Map<String, Player>,
    val lobby_name: String
)

data class Current(
    val correct_guesses: Int,
    val current_artist: String,
    val current_round: Int,
    val current_state: String,
    val current_turn: Int
)

data class Parameters(
    val category: String,
    val host_id: String,
    val nb_players: Int,
    val nb_rounds: Int
)

data class Player(
    val score: Int
)