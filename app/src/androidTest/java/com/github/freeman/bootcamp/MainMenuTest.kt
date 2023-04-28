package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.CreateJoinActivity
import com.github.freeman.bootcamp.games.guessit.chat.ChatActivity
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingActivity
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.videocall.VideoCallActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
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
        composeRule.onNode(hasTestTag("mainMenuScreen")).assertIsDisplayed()
    }

    @Test
    fun mainMenuScreenHasGameName() {
        composeRule.onNode(hasTestTag("gameName")).assertTextContains("Guess It!")
    }

    @Test
    fun playButtonTextIsCorrect() {
        composeRule.onNode(hasTestTag("playButton")).assertTextContains("Play game")
    }

    @Test
    fun playButtonHasClickAction() {
        composeRule.onNode(hasTestTag("playButton")).assertHasClickAction()
    }

    @Test
    fun playIntentIsSent() {
        Intents.init()

        composeRule.onNode(hasTestTag("playButton")).performClick()
        Intents.intended(IntentMatchers.hasComponent(CreateJoinActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun settingsButtonTextIsCorrect() {
        composeRule.onNode(hasTestTag("settingsButton")).assertTextContains("Settings")
    }

    @Test
    fun settingsButtonHasClickAction() {
        composeRule.onNode(hasTestTag("settingsButton")).assertHasClickAction()
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

        composeRule.onNodeWithText("Chat").performClick()
        Intents.intended(IntentMatchers.hasComponent(ChatActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun guessingIntentIsSent() {
        Intents.init()

        composeRule.onNodeWithText("Guessing").performClick()
        Intents.intended(IntentMatchers.hasComponent(GuessingActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun audioRecIntentIsSent() {
        Intents.init()

        composeRule.onNodeWithText("Audio Recording").performClick()
        Intents.intended(IntentMatchers.hasComponent(AudioRecordingActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun drawingIntentIsSent() {
        Intents.init()

        composeRule.onNodeWithText("Drawing").performClick()
        Intents.intended(IntentMatchers.hasComponent(DrawingActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun wordleButtonIsDisplayedHasClickActionAndCorrectText(){
        testButton("wordleButton", WordleMenu::class.java.name,MainMenuActivity.WORDLE)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun videoButtonIsDisplayedHasClickActionAndCorrectText(){
        testButton("videoCallButton", VideoCallActivity::class.java.name,MainMenuActivity.VIDEO_CALL)
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