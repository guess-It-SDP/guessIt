package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.Node.node
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.videocall.VideoCallActivity
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
    fun playButtonIsDisplayedHasRightTextIsClickableAndSendsIntent(){
        testButton("playButton", GameOptionsActivity::class.java.name,MainMenuActivity.PLAY)
    }

    @Test
    fun settingsButtonTextIsCorrect() {
        node("settingsButton").assertTextContains("Settings")
    }

    @Test
    fun settingsButtonHasClickAction() {
        node("settingsButton").assertHasClickAction()
    }

    @Test
    fun settingsIntentIsSent() {
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.startService(Intent(context, BackgroundMusicService::class.java))

        composeRule.onNodeWithText("Settings").performClick()
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun profileButtonIsDisplayedHasRightTextIsClickableAndSendsIntent(){
        testButton("profileButton", ProfileActivity::class.java.name,MainMenuActivity.PROFILE)
    }

    @Test
    fun chatButtonIsDisplayedHasRightTextIsClickableAndSendsIntent(){
        testButton("chatTestButton", ChatActivity::class.java.name,MainMenuActivity.CHAT)
    }

    @Test
    fun guessingButtonIsDisplayedHasRightTextIsClickableAndSendsIntent(){
        testButton("guessingButton", GuessingActivity::class.java.name,MainMenuActivity.GUESSING)
    }


    @Test
    fun audioRecButtonIsDisplayedHasRightTextIsClickableAndSendsIntent(){
        testButton("audioRecordingButton", AudioRecordingActivity::class.java.name,MainMenuActivity.AUDIO_REC)
    }

    @Test
    fun drawingButtonIsDisplayedHasRightTextIsClickableAndSendsIntent(){
        testButton("drawingButton", DrawingActivity::class.java.name,MainMenuActivity.DRAWING)
    }

    @Test
    fun signInButtonIsDisplayedHasRightTextIsClickableAndSendsIntent(){
    testButton("SignInButton", FirebaseAuthActivity::class.java.name,MainMenuActivity.SIGN_IN)
    }

    @Test
    fun videoCallButtonIsDisplayedHasRightTextIsClickableAndSendsIntent(){
        testButton("videoCallButton", VideoCallActivity::class.java.name,MainMenuActivity.VIDEO_CALL)
    }

    /**
     * Test is a button is displayed, has the right text, is clickable and sends the correct intents
     */
    private fun testButton(testTag: String, activityClassName: String, text:String){
        node(testTag).assertIsDisplayed().assertHasClickAction().assertTextContains(text)
        intentIsSend(testTag,activityClassName)
    }

    /**
     * Test that an intend is sent when clicking on a button with given test tag
     */
    private fun intentIsSend(testTag: String, activityClassName: String){
        Intents.init()
        node(testTag).performClick()
        Intents.intended(IntentMatchers.hasComponent(activityClassName))
        Intents.release()
    }

    private fun setMainMenuScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
    }

    /**
     * Return a node from a test Tag
     */
    private fun node(testTag: String): SemanticsNodeInteraction {
        return node(testTag,composeRule);
    }
}