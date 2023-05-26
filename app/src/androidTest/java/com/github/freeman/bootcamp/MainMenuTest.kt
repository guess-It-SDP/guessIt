package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.MainMenuActivity.Companion.CHAT
import com.github.freeman.bootcamp.MainMenuActivity.Companion.PLAY
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SETTINGS
import com.github.freeman.bootcamp.games.guessit.chat.ChatActivity
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setTheContentBefore(){
        setMainMenuScreen()
    }



    @Test
    fun mainMenuScreenIsDisplayed() {
        composeRule.onNodeWithTag("mainMenuScreen").assertIsDisplayed()
    }

    @Test
    fun mainMenuScreenHasGameName() {
        composeRule.onNodeWithTag("gameName").assertTextContains("Guess It!")
    }

    @Test
    fun playButtonTextIsCorrect() {
        composeRule.onNodeWithTag("playButton").assertTextContains(PLAY)
    }

    @Test
    fun playButtonHasClickAction() {
        composeRule.onNodeWithTag("playButton").assertHasClickAction()
    }

    @Test
    fun playIntentIsSent() {
        Intents.init()

        composeRule.onNodeWithTag("playButton").performClick()
        Intents.intended(IntentMatchers.hasComponent(CreateJoinActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun settingsButtonTextIsCorrect() {
        composeRule.onNodeWithTag("settingsButton").assertTextContains(SETTINGS)
    }

    @Test
    fun settingsButtonHasClickAction() {
        composeRule.onNodeWithTag("settingsButton").assertHasClickAction()
    }

    @Test
    fun chatTestButtonIsDisplayed() {
        composeRule.onNodeWithTag("chatButton").assertHasClickAction()
    }

    @Test
    fun clickingSettingsSendsIntent() {
        Intents.init()
        composeRule.onNodeWithTag("settingsButton").performClick()
        Intents.intended(IntentMatchers.hasComponent(SettingsProfileActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun chatTestIntentIsSent() {
        Intents.init()

        composeRule.onNodeWithText(CHAT).performClick()
        Intents.intended(IntentMatchers.hasComponent(ChatActivity::class.java.name))

        Intents.release()
    }

        /**
         * Test is a button is displayed, has the right text, is clickable and sends the correct intents
         */
        fun testButton(testTag: String, activityClassName: String, text: String) {
            node(testTag).assertIsDisplayed().assertHasClickAction().assertTextContains(text)
            intentIsSend(testTag, activityClassName)
        }

        /**
         * Test that an intend is sent when clicking on a button with given test tag
         */
        private fun intentIsSend(testTag: String, activityClassName: String) {
            Intents.init()
            node(testTag).performClick()
            Intents.intended(IntentMatchers.hasComponent(activityClassName))
            Intents.release()
        }

    /**
     * Return a node from a test Tag
     */
    private fun node(testTag: String): SemanticsNodeInteraction {
        return composeRule.onNodeWithTag(testTag)
    }

    private fun setMainMenuScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
    }
}