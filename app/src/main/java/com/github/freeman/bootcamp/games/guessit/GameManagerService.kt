package com.github.freeman.bootcamp.games.guessit

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
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
        var topics = ArrayList<String>()
        var firstStart = true
        var playersOrder = listOf<String>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (firstStart) {
            Log.d("GameManagerD", "Game Manager Started")
            val gameID = intent!!.getStringExtra(getString(R.string.gameId_extra)).toString()
            val gameDBRef = getGameDBRef(this, gameID)
            // Get the number of rounds and number of players
            FirebaseUtilities.databaseGet(gameDBRef.child(getString(R.string.param_nb_rounds_path)))
                .thenAccept { getNbRounds ->
                    nbRounds = getNbRounds.toInt()
                    FirebaseUtilities.databaseGet(gameDBRef.child(getString(R.string.param_nb_players_path)))
                        .thenAccept { getNbPlayers ->
                            nbPlayers = getNbPlayers.toInt()
                            // Get local player ID and host ID to check if this app is the host app
                            val localPlayerID = Firebase.auth.currentUser?.uid.toString()
                            FirebaseUtilities.databaseGet(gameDBRef.child(getString(R.string.param_host_id_path)))
                                .thenAccept { hostID ->
                                    isHost = localPlayerID == hostID.toString()

                                    // Get the list of topics
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
                                                    getString(R.string.state_initialize) -> {
                                                        if (isHost) {
                                                            initializeGame(gameDBRef)
                                                        }
                                                    }
                                                    getString(R.string.state_setartist) -> {
                                                        if (isHost) {
                                                            setNewArtist(gameDBRef)
                                                        }
                                                    }
                                                    getString(R.string.state_newturn) -> {
                                                        startNewTurn(localPlayerID, gameID, gameDBRef)
                                                    }
                                                    getString(R.string.state_scorerecap) -> {
                                                        scoreRecap(gameID)
                                                        prepareNewTurn(gameDBRef)
                                                    }
                                                    getString(R.string.state_gameover) -> {
                                                        gameOver(gameID)
                                                    }
                                                }
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            // do nothing
                                        }
                                    })

                                    // Change game state to score recap or game over if the timer is over
                                    val currentTimerRef = gameDBRef.child(getString(R.string.current_timer_path))
                                    currentTimerRef.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val timerState = snapshot.getValue<String>()!!
                                                if (timerState == getString(R.string.timer_over)) {
                                                    endTurn(currentStateRef, currentTimerRef)
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
                                                if (correctGuesses == nbPlayers - 1) {
                                                    endTurn(currentStateRef, currentTimerRef)
                                                }
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            // do nothing
                                        }
                                    })
                                }
                        }
                }
            firstStart = false
        }
        return START_NOT_STICKY
    }

    // Override required to extend Service
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun initializeGame(gameDBRef: DatabaseReference) {
        // Create players order that will be used for the rest of the game
        // First get all the player IDs
        FirebaseUtilities.databaseGetMap(gameDBRef.child(getString(R.string.players_path)))
            .thenAccept {
                // Randomly shuffle the player IDs
                @Suppress("UNCHECKED_CAST")
                playersOrder = it.keys.toList() as List<String>
                playersOrder = playersOrder.shuffled()
                Log.d("GameManagerD", "Players order: $playersOrder")
                // Change game state to start the game
                gameDBRef.child(getString(R.string.current_state_path)).setValue(getString(R.string.state_setartist))
                Log.d("GameManagerD", "New turn state set (initialization)")
            }
    }

    // Starts the correct activity between the Topic Selection Activity and the Guessing Activity
    private fun startNewTurn(localPlayerID : String, gameID : String, gameDBRef : DatabaseReference) {
        // Get current artist
        FirebaseUtilities.databaseGet(gameDBRef.child(getString(R.string.current_artist_path)))
            .thenAccept {
                val currentArtistID = it
                Log.d("GameManagerD", "Current Artist: $it")
                val intent : Intent
                if (localPlayerID == currentArtistID) {
                    // Launch the topic selection activity
                    intent = Intent(this, TopicSelectionActivity::class.java)
                    intent.apply {
                        for (i in topics.indices) {
                            putExtra("topic$i", topics[i])
                        }
                    }
                    Log.d("GameManagerD", "Topic selection launched")
                } else {
                    // Launch the guessing activity
                    intent = Intent(this, GuessingActivity::class.java)
                    Log.d("GameManagerD", "Guessing Activity launched")
                }
                intent.putExtra(getString(R.string.gameId_extra), gameID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }

    private fun scoreRecap(gameID: String) {
        val intent = Intent(this, ScoreActivity2::class.java)
        intent.putExtra(getString(R.string.gameId_extra), gameID)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun endTurn(currentStateRef: DatabaseReference, currentTimerRef: DatabaseReference) {
        Log.d("GameManagerD", "Round number : $roundNb, Turn number : $turnNb")
        currentTimerRef.setValue(getString(R.string.timer_unused))
        if (roundNb == nbRounds - 1 && turnNb == nbPlayers - 1) {
            Timer().schedule(2000) {
                currentStateRef.setValue(getString(R.string.state_gameover))
            }
        } else {
            Timer().schedule(2000) {
                currentStateRef.setValue(getString(R.string.state_scorerecap))
            }
        }
    }

    private fun prepareNewTurn(gameDBRef: DatabaseReference) {
        if (turnNb == nbPlayers - 1) {
            turnNb = 0
            roundNb += 1
        } else {
            turnNb += 1
        }
        if (isHost) {
//            setNewArtist(gameDBRef)
            // Set the number of correct guesses to 0
            gameDBRef.child(getString(R.string.current_correct_guesses_path)).setValue(0)
            // Delete all the guesses
            gameDBRef.child(getString(R.string.guesses_path)).removeValue()
            // Change game state to start new turn
            // (wait 10 seconds so that players have time to see their scores)
            Timer().schedule(10000) {
                gameDBRef.child(getString(R.string.current_state_path)).setValue(getString(R.string.state_setartist))
                Log.d("GameManagerD", "New turn state set")
            }
        }
    }

    private fun setNewArtist(gameDBRef : DatabaseReference) {
        Log.d("GameManagerD", "Setting new artist")
        Log.d("GameManagerD", "PO: $playersOrder")
        gameDBRef.child(getString(R.string.current_artist_path)).setValue(playersOrder[turnNb]).addOnSuccessListener {
            gameDBRef.child(getString(R.string.current_state_path)).setValue(getString(R.string.state_newturn))
        }
    }

    private fun gameOver(gameID: String) {
        if (isHost) {
            VideoCreator.createRecap(this, gameID)
        }
        val intent = Intent(this, FinalActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(getString(R.string.gameId_extra), gameID)
        startActivity(intent)
        stopSelf()
    }
}