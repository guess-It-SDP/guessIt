package com.github.freeman.bootcamp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.freeman.bootcamp.games.guessit.*
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.google.firebase.database.DatabaseReference
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
                    nb_rounds = 5
                ),
                Players = mapOf(Pair("test_player_id", Player(0))),
                lobby_name = "test's room"
            )

            dbRef.child(context.getString(R.string.games_path))
                .child(context.getString(R.string.test_game_id))
                .setValue(gameData)

            BootcampComposeTheme {
                Column {
                    TopAppbarLobbies()

                    Surface(
                        modifier = Modifier.fillMaxSize(), color = Color(0xFFF1F1F1)
                    ) {
                        LobbyList(dbRef)
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