package com.github.freeman.bootcamp

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.freeman.bootcamp.games.guessit.*
import com.github.freeman.bootcamp.games.guessit.lobbies.*
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LobbyListTest {

    private fun initDatabase(): DatabaseReference {
        FirebaseEmulator.init()
        return FirebaseSingletons.database.get().database.reference
    }

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initScreen() {
        composeRule.setContent {
            val context = LocalContext.current

            val dbRef = initDatabase()

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
                    nb_rounds = 5,
                    type = "public",
                    password = ""
                ),
                Players = mapOf(Pair("test_player_id", Player(0, false))),
                lobby_name = "test's room"
            )

            getGameDBRef(context).setValue(gameData)

            BootcampComposeTheme {
                val password = remember { mutableStateOf("") }
                val enterPassword = remember { mutableStateOf(false) }
                val lobby = remember { mutableStateOf(Lobby("", "", 0, 0, "", "")) }

                Column {
                    TopAppbarLobbies()

                    androidx.compose.material.Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFFF1F1F1)
                    ) {
                        if (!enterPassword.value) {
                            LobbyList(dbRef, enterPassword, lobby)
                        }
                    }

                    if (enterPassword.value) {
                        val userId = Firebase.auth.uid
                        EditDialog(password,
                            LobbyListActivity.ENTER_PASSWORD_TEXT,
                            LobbyListActivity.PASSWORD_TEXT, enterPassword) {
                            if (lobby.value.password == it) {
                                joinLobby(context, dbRef.child(context.getString(R.string.games_path)), userId.toString(), lobby.value)
                            } else {
                                Toast.makeText(context,
                                    LobbyListActivity.WRONG_PASSWORD_TEXT, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun topAppBarLobbiesIsDisplayed() {
        composeRule.onNodeWithTag("topAppbarLobbies").assertIsDisplayed()
    }

    @Test
    fun listItemIsDisplayed() {
        composeRule.onAllNodesWithTag("listItem").assertAll(isEnabled())
    }

    @Test
    fun lobbyListIsDisplayed() {
        composeRule.onNodeWithTag("lobbyList").assertIsDisplayed()
    }
}