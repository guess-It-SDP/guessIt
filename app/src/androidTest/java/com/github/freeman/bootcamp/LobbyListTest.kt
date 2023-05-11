package com.github.freeman.bootcamp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.freeman.bootcamp.games.guessit.*
import com.github.freeman.bootcamp.games.guessit.lobbies.*
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LobbyListTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initScreen() {
        composeRule.setContent {
            val context = LocalContext.current

            val gameDataPublic = GameData(
                Current = Current(
                    correct_guesses = 0,
                    current_artist = "test_artist_id_1",
                    current_round = 0,
                    current_state = "waiting for players",
                    current_turn = 0,
                    current_timer = "unused"
                ),
                Parameters = Parameters(
                    category = "Objects",
                    host_id = "test_host_id_1",
                    nb_players = 1,
                    nb_rounds = 5,
                    type = "public",
                    password = ""
                ),
                Players = mapOf(Pair("test_player_id_1", Player(0, false))),
                lobby_name = "test_1's room"
            )

            val gameDataPrivate = GameData(
                Current = Current(
                    correct_guesses = 0,
                    current_artist = "test_artist_id_2",
                    current_round = 0,
                    current_state = "waiting for players",
                    current_turn = 0,
                    current_timer = "unused"
                ),
                Parameters = Parameters(
                    category = "Objects",
                    host_id = "test_host_id_2",
                    nb_players = 1,
                    nb_rounds = 5,
                    type = "private",
                    password = "abc"
                ),
                Players = mapOf(Pair("test_player_id_2", Player(0, false))),
                lobby_name = "test_2's room"
            )

            getGameDBRef(context, "testPublicGameId").setValue(gameDataPublic)
            getGameDBRef(context, "testPrivateGameId").setValue(gameDataPrivate)

            BootcampComposeTheme {
                MainMenuScreen()
            }
        }

        composeRule.onNodeWithTag("playButton").performClick()
        composeRule.onNodeWithTag("joiningGameButton").performClick()
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

    @Test
    fun enteringRightPasswordEntersLobby() {
        Intents.init()

        composeRule.onNodeWithText("test_2's room").performClick()
        composeRule.onNodeWithTag("dialogTextField").performTextInput("abc")
        composeRule.onNodeWithTag("doneButton").performClick()

        Intents.intended(IntentMatchers.hasComponent(WaitingRoomActivity::class.java.name))


        Intents.release()
    }
}