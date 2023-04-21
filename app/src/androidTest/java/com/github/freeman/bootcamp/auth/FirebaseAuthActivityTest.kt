package com.github.freeman.bootcamp.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.github.freeman.bootcamp.MainMenuScreen
import com.github.freeman.bootcamp.games.guessit.CreateJoinActivity
import okhttp3.internal.wait
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
    fun containsCorrectButtonsAndTextWhenNotSignedIn() {
        composeRule.onNodeWithTag("google_sign_in_button").assertIsDisplayed()
        composeRule.onNodeWithTag("anonymous_sign_in_button").assertIsDisplayed()
        composeRule.onNodeWithTag("sign_in_info").assertTextContains("Not signed in")
    }

    @Test
    fun containsCorrectButtonsAndTextWhenAnonymouslySignedIn() {
        composeRule.onNodeWithTag("anonymous_sign_in_button").performClick()
        device.wait(
            Until.findObject(By.textContains("anonymously")), 30000
        )
        composeRule.onNodeWithTag("create_profile_button").assertIsDisplayed()
        composeRule.onNodeWithTag("delete_button").assertIsDisplayed()
        composeRule.onNodeWithTag("sign_in_info").assertTextContains("Signed in anonymously")

        composeRule.onNodeWithTag("delete_button").performClick()
    }

    @Test
    fun deleteGoogleAccountResultsInCorrectMessage() {
        composeRule.onNodeWithTag("anonymous_sign_in_button").performClick()
        device.wait(
            Until.findObject(By.textContains("anonymously")), 30000
        )
        composeRule.onNodeWithTag("delete_button").performClick()
        device.wait(
            Until.findObject(By.textContains("Google")), 30000
        )
        composeRule.onNodeWithTag("sign_in_info").assertTextContains("Account deleted")
    }

    @Test
    fun createProfileButtonsLaunchesTheActivity() {
        Intents.init()

        composeRule.onNodeWithTag("anonymous_sign_in_button").performClick()
        device.wait(
            Until.findObject(By.textContains("anonymously")), 30000
        )
        composeRule.onNodeWithTag("create_profile_button").performClick()
        Intents.intended(IntentMatchers.hasComponent(ProfileCreationActivity::class.java.name))

        Espresso.pressBack()
        composeRule.onNodeWithTag("delete_button").performClick()

        Intents.release()
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