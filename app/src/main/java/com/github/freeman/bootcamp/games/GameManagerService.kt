package com.github.freeman.bootcamp.games

import android.app.Service
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity
import com.github.freeman.bootcamp.games.guessit.ScoreActivity
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

/***
 * This Service is launched from the WaitingRoomActivity when a game is started,
 * either because this app is the game host and the Start button was clicked,
 * or because this app was in a lobby and the game state changed to "new turn".
 * This service manages the transitions between the different activities and updates
 * the database when needed throughout the span of the game.
 */
class GameManagerService : Service() {

    companion object {
        var roundNb = 0
        var turnNb = 0
        var nbPlayers = 0
        var nbRounds = 0
        var isHost = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val gameID = intent!!.getStringExtra(getString(R.string.gameId_extra)).toString()
        val gameDBRef = getGameDBRef(this, gameID)
        var playersOrder = listOf<String>()
        // Get the number of rounds and number of players
        Log.d("NB ROUNDS", "BEFORE GET")
        FirebaseUtilities.databaseGet(gameDBRef.child(getString(R.string.param_nb_rounds_path)))
            .thenAccept {
                nbRounds = it.toInt()
                Log.d("NB ROUNDS", nbRounds.toString())
            }
        Log.d("NB ROUNDS", nbRounds.toString())
        Log.d("NB ROUNDS", "AFTER GET")
        FirebaseUtilities.databaseGet(gameDBRef.child(getString(R.string.param_nb_players_path)))
            .thenAccept {
                nbPlayers = it.toInt()
            }

        // Get local player ID and host ID to check if this app is the host app
        val localPlayerID = Firebase.auth.currentUser?.uid.toString()
        Log.d("LOCAL PLAYER ID", localPlayerID)
        var hostID = ""
        gameDBRef.child(getString(R.string.param_host_id_path)).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        hostID = snapshot.getValue<String>()!!
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // do nothing
                }
            }
        )
        Log.d("HOST ID", hostID)
        isHost = localPlayerID == hostID

        // Get the list of topics
        val topics = ArrayList<String>()
        for (i in 0 until GameOptionsActivity.NB_TOPICS) {
            topics.add(intent.getStringExtra("topic$i").toString())
        }

        // Listen to the current game state to start corresponding activities
        // Activities are responsible for closing themselves based on current the game state
        val currentStateRef = gameDBRef.child(getString(R.string.current_state_path))
        currentStateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    when (snapshot.getValue<String>()!!) {
                        getString(R.string.state_newturn) -> {
                            startNewTurn(localPlayerID, gameID, gameDBRef, topics)
                        }
                        getString(R.string.state_scorerecap) -> {
                            scoreRecap()
                            prepareNewTurn(gameDBRef, playersOrder)
                        }
                        getString(R.string.state_gameover) -> {
                            gameOver(gameDBRef)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }
        })

        // Change game state to score recap or game over if the timer is over
        val timerStateRef = gameDBRef.child(getString(R.string.current_timer_path))
        timerStateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val timerState = snapshot.getValue<String>()!!
                    if (timerState == getString(R.string.timer_over)) {
                        if (roundNb < nbRounds) {
                            Timer().schedule(2000) {
                                currentStateRef.setValue(getString(R.string.state_scorerecap))
                            }
                        } else if (roundNb == nbRounds && turnNb == nbPlayers) {
                            Timer().schedule(2000) {
                                currentStateRef.setValue(getString(R.string.state_gameover))
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }
        })

        // Change game state to score recap or game over if everyone guessed the word
        val correctGuessesRef = gameDBRef.child(getString(R.string.current_correct_guesses_path))
        correctGuessesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val correctGuesses = snapshot.getValue<Long>()!!.toInt()
                    if (correctGuesses == nbPlayers) {
                        if (roundNb < nbRounds) {
                            Timer().schedule(2000) {
                                currentStateRef.setValue(getString(R.string.state_scorerecap))
                            }
                        } else if (roundNb == nbRounds && turnNb == nbPlayers - 1) {
                            Timer().schedule(2000) {
                                currentStateRef.setValue(getString(R.string.state_gameover))
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }
        })

        // Initialize the game if this app is the host app
        if (isHost) {
            // Create players order that will be used for the rest of the game
            // First get all the player IDs
            var players = mapOf<String, Map<String, Int>>()
            FirebaseUtilities.databaseGetMap(gameDBRef.child(getString(R.string.players_path)))
                .thenAccept {
                    players = it as HashMap<String, Map<String, Int>>
                }
            // Then randomly shuffle the player IDs
            playersOrder = players.keys.toList().shuffled()
            Log.d("PLAYERSORDER[0]", playersOrder[0])
            // Set first player to draw
            setNewArtist(gameDBRef, 0, playersOrder)
            // Change game state to start the game
            gameDBRef.child(getString(R.string.current_state_path)).setValue(getString(R.string.state_newturn))
        }

        return START_NOT_STICKY
    }

    // Override required to extend Service
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // Starts the correct activity between the Topic Selection Activity and the Guessing Activity
    private fun startNewTurn(localPlayerID : String, gameID : String, gameDBRef : DatabaseReference, topics : List<String>) {
        // Get current artist
        var currentArtistID = ""
        FirebaseUtilities.databaseGet(gameDBRef.child(getString(R.string.current_artist_path)))
            .thenAccept {
                currentArtistID = it
            }
        val intent : Intent
        if (localPlayerID == currentArtistID) {
            // Launch the topic selection activity
            intent = Intent(this, TopicSelectionActivity::class.java)
            intent.apply {
                for (i in topics.indices) {
                    putExtra("topic$i", topics[i])
                }
            }
        } else {
            // Launch the guessing activity
            intent = Intent(this, GuessingActivity::class.java)
        }
        intent.putExtra(getString(R.string.gameId_extra), gameID)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun scoreRecap() {
        val intent = Intent(this, ScoreActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun prepareNewTurn(gameDBRef: DatabaseReference, playersOrder : List<String>) {
        if (turnNb == nbPlayers - 1) {
            turnNb = 0
            roundNb += 1
        } else {
            turnNb += 1
        }
        if (isHost) {
            setNewArtist(gameDBRef, turnNb, playersOrder)
            // Set the number of correct guesses to 0
            gameDBRef.child(getString(R.string.current_correct_guesses_path)).setValue(0)
            // Delete all the guesses
            gameDBRef.child(getString(R.string.guesses_path)).removeValue()
            // Change game state to start new turn
            // (wait 10 seconds so that players have time to see their scores)
            Timer().schedule(10000) {
                gameDBRef.child(getString(R.string.current_state_path)).setValue(getString(R.string.state_newturn))
            }
        }
    }

    private fun setNewArtist(gameDBRef : DatabaseReference, playerNumber : Int, playersOrder : List<String>) {
        gameDBRef.child(getString(R.string.current_artist_path)).setValue(playersOrder[playerNumber])
    }

    private fun gameOver(gameDBRef : DatabaseReference) {
        val intent = Intent(this, ScoreActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        saveStatsToProfile()
        gameDBRef.child(getString(R.string.current_state_path)).setValue(getString(R.string.state_lobbyclosed))
        stopSelf()
    }

    private fun saveStatsToProfile() {

    }
}