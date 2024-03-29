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
    val current_state: String, //can be : "waiting for players", "topic selection", "play round", "play game", "lobby closed"
    val current_turn: Int,
    val current_timer: String //can be : "over", "inprogress", "unused"
)

data class Parameters(
    val type: String,
    val password: String,
    val category: String,
    val host_id: String,
    val nb_players: Int,
    val nb_rounds: Int
)

data class Player(
    val score: Int,
    val kicked: Boolean
)