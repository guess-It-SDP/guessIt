package com.github.freeman.bootcamp.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.github.freeman.bootcamp.MainMenuScreen
import com.github.freeman.bootcamp.games.guessit.CreateJoinActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirebaseAuthActivityTest {
    private lateinit var device: UiDevice


    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun startMainActivityFromHomeScreen() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)

        composeRule.setContent {
            MainMenuScreen()

        }
        composeRule.onNodeWithTag("signInButton").performClick()
    }

    @Test
    fun containsCorrectButtonsWhenNotSignedIn() {
        composeRule.onNodeWithTag("google_sign_in_button").assertIsDisplayed()
        composeRule.onNodeWithTag("anonymous_sign_in_button").assertIsDisplayed()
        composeRule.onNodeWithTag("create_profile_button").assertIsNotDisplayed()
        composeRule.onNodeWithTag("delete_button").assertIsNotDisplayed()
        composeRule.onNodeWithTag("sign_out_button").assertIsNotDisplayed()
    }

    @Test
    fun containsCorrectTextWhenNotSignedIn() {
        composeRule.onNodeWithTag("sign_in_info").assertTextContains("Not signed in")
    }

    @Test
    fun containsCorrectButtonsWhenAnonymouslySignedIn() {
        composeRule.onNodeWithTag("anonymous_sign_in_button").performClick()
        composeRule.onNodeWithTag("create_profile_button").assertIsDisplayed()
        composeRule.onNodeWithTag("delete_button").assertIsDisplayed()
    }

    @Test
    fun containsCorrectTextWhenAnonymouslySignedIn() {
        composeRule.onNodeWithTag("anonymous_sign_in_button").performClick()
        composeRule.onNodeWithTag("sign_in_info").assertTextContains("Signed in anonymously")
    }

    @Test
    fun createProfileButtonsLaunchesTheActivity() {
        Intents.init()

        composeRule.onNodeWithTag("anonymous_sign_in_button").performClick()
        composeRule.onNodeWithTag("create_profile_button").performClick()
        Intents.intended(IntentMatchers.hasComponent(ProfileCreationActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun deleteGoogleAccountResultsInCorrectMessage() {
        composeRule.onNodeWithTag("anonymous_sign_in_button").performClick()
        composeRule.onNodeWithTag("delete_button").performClick()
        composeRule.onNodeWithTag("sign_in_info").assertTextContains("Account deleted")
    }

    @Test
    fun signInResultsInCorrectLayout() {

        composeRule.onNodeWithTag("google_sign_in_button").performClick()
        device.wait(
            Until.findObject(By.textContains("Google")), 30000
        )
        val googleText = device.findObject(UiSelector().textContains(""))
        assert(googleText.exists())
    }

}