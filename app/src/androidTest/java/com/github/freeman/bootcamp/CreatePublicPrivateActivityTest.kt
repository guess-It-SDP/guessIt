package com.github.freeman.bootcamp

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity
import com.github.freeman.bootcamp.games.guessit.lobbies.PublicPrivateLobbyScreen
import com.github.freeman.bootcamp.games.guessit.lobbies.TopAppbarPublicPrivate
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreatePublicPrivateActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                TopAppbarPublicPrivate()
                PublicPrivateLobbyScreen()
            }
        }
    }

    @Test
    fun mainScreenIsDisplayed() {
        composeRule.onNodeWithTag("createPublicPrivateMainScreen").assertIsDisplayed()
    }

    @Test
    fun topAppBarIsDisplayed() {
        composeRule.onNodeWithTag("topAppbarPublicPrivate").assertIsDisplayed()
    }

    @Test
    fun topAppBarCanBeClicked() {
        composeRule.onNodeWithTag("topAppbarPublicPrivateButton").assertHasClickAction()
        composeRule.onNodeWithTag("topAppbarPublicPrivateButton").performClick()
        composeRule.onNodeWithTag("topAppbarPublicPrivateButton").assertDoesNotExist()
    }

    @Test
    fun publicButtonIsDisplayed() {
        composeRule.onNodeWithTag("publicLobbyButton").assertIsDisplayed()
    }

    @Test
    fun publicButtonCanBeClicked() {
        composeRule.onNodeWithTag("publicLobbyButton").assertHasClickAction()
        composeRule.onNodeWithTag("publicLobbyButton").performClick()
        composeRule.onNodeWithTag("publicLobbyButton").assertDoesNotExist()
    }

    @Test
    fun privateButtonIsDisplayed() {
        composeRule.onNodeWithTag("privateLobbyButton").assertIsDisplayed()
    }

    @Test
    fun privateButtonCanBeClicked() {
        composeRule.onNodeWithTag("privateLobbyButton").assertHasClickAction()
        composeRule.onNodeWithTag("privateLobbyButton").performClick()
        composeRule.onNodeWithTag("privateLobbyButton").assertDoesNotExist()
    }

    @Test
    fun publicButtonSendsIntent() {
        Intents.init()

        composeRule.onNodeWithTag("publicLobbyButton").performClick()
        Intents.intended(IntentMatchers.hasComponent(GameOptionsActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun privateButtonSendsIntent() {
        Intents.init()

        composeRule.onNodeWithTag("privateLobbyButton").performClick()
        Intents.intended(IntentMatchers.hasComponent(GameOptionsActivity::class.java.name))

        Intents.release()
    }
}