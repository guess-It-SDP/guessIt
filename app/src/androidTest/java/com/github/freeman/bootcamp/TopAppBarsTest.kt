package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.MainMenuActivity.Companion.PLAY
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SETTINGS
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity
import com.github.freeman.bootcamp.games.guessit.chat.ChatActivity
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity
import com.github.freeman.bootcamp.games.guessit.lobbies.LobbyListActivity
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopAppBarsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initFromMain() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
    }

    /*
    @Test
    fun chatTopAppBar() {
        composeRule.onNodeWithText("Chat").performClick()

        composeRule.onNodeWithTag("topAppbarChat").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarChatTitle").assertTextContains(ChatActivity.GLOBAL_CHAT_TILE)
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }
    */

    @Test
    fun settingsTopAppBar() {
        composeRule.onNodeWithText(SETTINGS).performClick()

        composeRule.onNodeWithTag("topAppbarProfile").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarProfileTitle").assertTextContains(
            SettingsActivity.SETTINGS_TITLE
        )
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

    @Test
    fun editProfileTopAppBar() {
        composeRule.onNodeWithText(SETTINGS).performClick()
        composeRule.onNodeWithTag("editProfileButton").performClick()

        composeRule.onNodeWithTag("topAppbarEditProfile").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarEditProfileTitle").assertTextContains(
            EditProfileActivity.TOPBAR_TEXT
        )
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

    /*
    @Test
    fun parametersTopAppBar() {
        composeRule.onNodeWithText(SETTINGS).performClick()
        composeRule.onNodeWithText("Parameters").performClick()

        composeRule.onNodeWithTag("topAppbarSettings").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarSettingsTitle").assertTextContains(
            SettingsActivity.PARAMETERS_TITLE
        )
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

     */

    @Test
    fun authTopAppBar() {
        composeRule.onNodeWithText(SETTINGS).performClick()
        composeRule.onNodeWithText("Manage Account").performClick()

        composeRule.onNodeWithTag("topAppbarAccount").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarAccountTitle").assertTextContains(FirebaseAuthActivity.SCREEN_TITLE)
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

    @Test
    fun drawHatTopAppBar() {
        composeRule.onNodeWithText(SETTINGS).performClick()
        composeRule.onNodeWithText("Draw Your Hat").performClick()

        composeRule.onNodeWithTag("topAppbarDrawHat").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarDrawHatTitle").assertTextContains(DrawHatActivity.DRAW_HAT)
        composeRule.onNodeWithTag("drawHatBackButton").assertIsDisplayed()
        composeRule.onNodeWithTag("drawHatBackButton").assertHasClickAction()
    }

    @Test
    fun gamesMenuTopAppBar() {
        composeRule.onNodeWithText(PLAY).performClick()

        composeRule.onNodeWithTag("topAppbarGamesMenu").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarGamesMenuTitle").assertTextContains(GamesMenuActivity.GAMES_MENU_TITLE)
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

    @Test
    fun createJoinTopAppBar() {
        composeRule.onNodeWithText(PLAY).performClick()
        composeRule.onNodeWithText("Guess It!").performClick()

        composeRule.onNodeWithTag("topAppbarCreateJoin").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarCreateJoinTitle").assertTextContains(
            CreateJoinActivity.TOPBAR_TEXT
        )
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

    @Test
    fun publicPrivateLobbyTopAppBar() {
        composeRule.onNodeWithText(PLAY).performClick()
        composeRule.onNodeWithText("Guess It!").performClick()
        composeRule.onNodeWithText("Create", substring = true).performClick()

        composeRule.onNodeWithTag("topAppbarPublicPrivate").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarPublicPrivateTitle").assertTextContains(
            CreatePublicPrivateActivity.TOPBAR_TEXT
        )
        composeRule.onNodeWithTag("topAppbarPublicPrivateButton").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarPublicPrivateButton").assertHasClickAction()
    }

    @Test
    fun gameOptionsTopAppBar() {
        composeRule.onNodeWithText(PLAY).performClick()
        composeRule.onNodeWithText("Guess It!").performClick()
        composeRule.onNodeWithText("Create", substring = true).performClick()
        composeRule.onNodeWithText("Public", substring = true).performClick()

        composeRule.onNodeWithTag("topBarGameOptions").assertIsDisplayed()
        composeRule.onNodeWithTag("topBarGameOptionsTitle").assertTextContains(
            GameOptionsActivity.TOPBAR_GAMEOPTIONS_TEXT
        )
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

    @Test
    fun lobbiesListTopAppBar() {
        composeRule.onNodeWithText(PLAY).performClick()
        composeRule.onNodeWithText("Guess It!").performClick()
        composeRule.onNodeWithText("Join", substring = true).performClick()

        composeRule.onNodeWithTag("topAppbarLobbies").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarLobbiesTitle").assertTextContains(
            LobbyListActivity.TOPBAR_TEXT
        )
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

    @Test
    fun wordleMenuTopAppBar() {
        composeRule.onNodeWithText(PLAY).performClick()
        composeRule.onNodeWithText("Wordle").performClick()

        composeRule.onNodeWithTag("topAppbarWordleMenu").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarWordleMenuTitle").assertTextContains(
            WordleMenu.WORDLE_MENU_TITLE
        )
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }

    @Test
    fun wordleGameTopAppBar() {
        composeRule.onNodeWithText(PLAY).performClick()
        composeRule.onNodeWithText("Wordle").performClick()
        composeRule.onNodeWithText("Easy", substring = true).performClick()

        composeRule.onNodeWithTag("topAppbarWordleGame").assertIsDisplayed()
        composeRule.onNodeWithTag("topAppbarWordleGameTitle").assertTextContains(
            WordleGameActivity.WORDLE_GAME_TITLE
        )
        composeRule.onNodeWithTag("appBarBack").assertIsDisplayed()
        composeRule.onNodeWithTag("appBarBack").assertHasClickAction()
    }
}