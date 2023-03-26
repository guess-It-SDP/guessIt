package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.wordle.WordleGameActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mainMenuScreenIsDisplayed() {
        setMainMenuScreen()
        composeRule.onNode(hasTestTag("mainMenuScreen")).assertIsDisplayed()
    }

    @Test
    fun mainMenuScreenHasGameName() {
        setMainMenuScreen()
        composeRule.onNode(hasTestTag("gameName")).assertTextContains("Guess It!")
    }

    @Test
    fun playButtonTextIsCorrect() {
        setMainMenuScreen()
        composeRule.onNode(hasTestTag("playButton")).assertTextContains("Play game")
    }

    @Test
    fun playButtonHasClickAction() {
        setMainMenuScreen()
        composeRule.onNode(hasTestTag("playButton")).assertHasClickAction()
    }

    @Test
    fun playIntentIsSent() {
        Intents.init()
        setMainMenuScreen()

        composeRule.onNode(hasTestTag("playButton")).performClick()
        Intents.intended(IntentMatchers.hasComponent(GameOptionsActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun settingsButtonTextIsCorrect() {
        setMainMenuScreen()
        composeRule.onNode(hasTestTag("settingsButton")).assertTextContains("Settings")
    }

    @Test
    fun settingsButtonHasClickAction() {
        setMainMenuScreen()
        composeRule.onNode(hasTestTag("settingsButton")).assertHasClickAction()
    }

    @Test
    fun settingsIntentIsSent() {
        Intents.init()
        setMainMenuScreen()
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.startService(Intent(context, BackgroundMusicService::class.java))

        composeRule.onNodeWithText("Settings").performClick()
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun profileButtonTextIsCorrect() {
        setMainMenuScreen()
        composeRule.onNode(hasTestTag("profileButton")).assertTextContains("Profile")
    }

    @Test
    fun profileButtonHasClickAction() {
        setMainMenuScreen()
        composeRule.onNode(hasTestTag("profileButton")).assertHasClickAction()
    }

    @Test
    fun profileIntentIsSent() {
        Intents.init()
        setMainMenuScreen()

        composeRule.onNodeWithText("Profile").performClick()
        Intents.intended(IntentMatchers.hasComponent(ProfileActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun chatTestButtonIsDisplayed() {
        setMainMenuScreen()
        composeRule.onNodeWithTag("chatTestButton").assertHasClickAction()
    }

    @Test
    fun chatTestIntentIsSent() {
        Intents.init()
        setMainMenuScreen()

        composeRule.onNodeWithText("Chat").performClick()
        Intents.intended(IntentMatchers.hasComponent(ChatActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun wordleButtonIsDisplayedClickable() {
        setMainMenuScreen()
        composeRule.onNodeWithTag("wordleButton").assertHasClickAction()
    }



    @Test
    fun wordleIntentIsSent() {
        Intents.init()
        setMainMenuScreen()

        composeRule.onNode(hasTestTag("wordleButton")).performClick()
        Intents.intended(IntentMatchers.hasComponent(WordleGameActivity::class.java.name))

        Intents.release()
    }



    private fun setMainMenuScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
    }
}