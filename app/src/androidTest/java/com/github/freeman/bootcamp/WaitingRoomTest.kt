package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.freeman.bootcamp.games.guessit.*
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.databaseGet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WaitingRoomTest {
    lateinit var allPlayers: SnapshotStateList<String>

    private fun initFirebase(): Pair<DatabaseReference, StorageReference> {
        FirebaseEmulator.init()
        return Pair(FirebaseSingletons.database.get().database.reference, FirebaseSingletons.storage.get().storage.reference)
    }

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initScreen() {
        val userId = Firebase.auth.uid

        val gameId = "test_game_id"
        val allTopics = ArrayList<String>()

        val firebase = initFirebase()
        val database = firebase.first
        val dbRef = database.child("games/$gameId")

        val profileMap1 = mapOf(Pair("email", "test@email.abc"), Pair("username", "test_username"))
        val profileMap2 = mapOf(Pair("email", "test2@email.abc"), Pair("username", "test_username_2"))
        val profileMap = mapOf(Pair("test_profile_id_1", profileMap1), Pair("test_profile_id_2", profileMap2))

        val gameData = GameData(
            Current = Current(
                correct_guesses = 0,
                current_artist = "test_artist_id",
                current_round = 0,
                current_state = "waiting for players",
                current_turn = 0,
                current_timer = "unused"
            ),
            Parameters = Parameters(
                category = "Objects",
                host_id = "test_host_id",
                nb_players = 1,
                nb_rounds = 5
            ),
            Players = mapOf(Pair("test_profile_id_1", Player(0, false)), Pair("test_profile_id_2", Player(0, false))),
            lobby_name = "test's room"
        )

        database.child("games/test_game_id").setValue(gameData)

        database.child("profiles").setValue(profileMap)

        val storage = firebase.second

        for (i in 0 until GameOptionsActivity.NB_TOPICS) {
            allTopics.add("test_topic_$i")
        }

        composeRule.setContent {
            val context = LocalContext.current

            val hostId = remember { mutableStateOf("") }
            databaseGet(dbRef.child("parameters/host_id")).thenAccept {
                hostId.value = it
            }

            val players = remember { mutableStateListOf<String>() }
            allPlayers = remember { mutableStateListOf<String>() }

            val gameStateRef = dbRef.child("current/current_state")
            val artistRef = dbRef.child("current/current_artist")
            val dbListener = gameStateRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val gameState = snapshot.getValue<String>()!!
                        if (gameState == "play game") {
                            databaseGet(artistRef)
                                .thenAccept {
                                    val intent = if (userId == it) {
                                        Intent(context, TopicSelectionActivity::class.java)
                                    } else {
                                        Intent(context, GuessingActivity::class.java)
                                    }

                                    intent.apply {
                                        putExtra("gameId", gameId)
                                        for (i in allTopics.indices) {
                                            putExtra("topic$i", allTopics[i])
                                        }
                                    }

                                    context.startActivity(intent)
                                }
                        } else if (gameState == "lobby closed") {
                            dbRef.removeValue()
                            val activity = (context as? Activity)
                            activity?.finish()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // do nothing
                }
            })

            val playersRef = database.child("games/$gameId/players")
            val playersListener = playersRef.addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key !in players) {
                        Toast.makeText(context, "player added", Toast.LENGTH_SHORT).show()
                        players.add(snapshot.key!!)
                        allPlayers.add(snapshot.key!!)

                        database.child("games/$gameId/parameters/nb_players").setValue(players.size)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Toast.makeText(context, "player changed", Toast.LENGTH_SHORT).show()
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Toast.makeText(context, "player removed", Toast.LENGTH_SHORT).show()
                    players.remove(snapshot.key)
                    allPlayers.remove(snapshot.key)
                    if (snapshot.key == hostId.value && !players.isEmpty()) {
                        val newHost = players.toList()[0]
                        database.child("games/$gameId/parameters/host_id").setValue(players.toList()[0])
                        hostId.value = newHost
                    }
                    database.child("games/$gameId/parameters/nb_players").setValue(players.size)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Toast.makeText(context, "player moved", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "player cancelled", Toast.LENGTH_SHORT).show()
                }
            })

            BootcampComposeTheme {
                Column{
                    TopAppbarWaitingRoom(
                        dbRef = dbRef,
                        hostId = hostId,
                        players = players,
                        dbListener = dbListener,
                        playersListener = playersListener
                    )

                    RoomInfo(
                        dbRef = dbRef
                    )

                    PlayerList(
                        modifier = Modifier.weight(1f),
                        dbRef = database,
                        storageRef = storage,
                        players = players,
                        hostId = hostId.value,
                        gameId = gameId
                    )

                    StartButton(
                        dbRef = dbRef,
                        players = players
                    )
                }
            }

            BackHandler {

                if (userId == hostId.value && players.size == 1) {
                    gameStateRef.setValue("lobby closed")
                } else {
                    dbRef.child("players/$userId").removeValue()
                }

                dbRef.removeEventListener(dbListener)
                playersRef.removeEventListener(playersListener)

                val activity = (context as? Activity)
                activity?.finish()
            }
        }
    }

    @Test
    fun waitingRoomTopAppBarIsDisplayed() {
        composeRule.onNodeWithTag("topAppbarWaitingRoom").assertIsDisplayed()
    }

    @Test
    fun waitingRoomTopAppBarBackIsClickable() {
        composeRule.onNodeWithTag("topAppBarBack").performClick()
    }

    @Test
    fun roomInfoIsDisplayed() {
        composeRule.onNodeWithTag("roomInfo").assertIsDisplayed()
    }

    @Test
    fun playerListIsDisplayed() {
        composeRule.onNodeWithTag("playerList").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun playerDisplayIsDisplayed() {
        composeRule.waitUntilAtLeastOneExists(hasTestTag("playerDisplay"), 10000)
        composeRule.onAllNodesWithTag("playerDisplay").assertAll(isEnabled())
    }

    @Test
    fun startButtonIsDisplayed() {
        composeRule.onNodeWithTag("startButton").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun startButtonIsClickable() {
        composeRule.waitUntilAtLeastOneExists(hasTestTag("playerDisplay"), 10000)

        composeRule.onNodeWithTag("startButton").performClick()
        composeRule.onNodeWithTag("startButton").assertHasClickAction()
    }

    // Note: the kick buttons are displayed for everyone, but are only visible to the host
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun kickButtonsAreDisplayed() {
        var i = 1
        for (p in allPlayers) {
            composeRule.waitUntilExactlyOneExists((hasTestTag("kickButton" + allPlayers[i-1])), 10000)
            composeRule.onNodeWithTag("kickButton" + allPlayers[i-1]).assertIsDisplayed()
            i += 1
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun kickButtonsAreClickable() {
        var i = 1
        for (p in allPlayers) {
            composeRule.waitUntilExactlyOneExists((hasTestTag("kickButton" + allPlayers[i-1])), 10000)
            composeRule.onNodeWithTag("kickButton" + allPlayers[i-1]).performClick()
            i += 1
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun kickButtonsAreDisabled() {
        var i = 1
        for (p in allPlayers) {
            composeRule.waitUntilExactlyOneExists((hasTestTag("kickButton" + allPlayers[i-1])), 10000)
            composeRule.onNodeWithTag("kickButton" + allPlayers[i-1]).assertIsNotEnabled()
            i += 1
        }
    }
}