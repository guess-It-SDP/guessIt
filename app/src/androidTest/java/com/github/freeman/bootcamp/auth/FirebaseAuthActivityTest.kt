package com.github.freeman.bootcamp.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.github.freeman.bootcamp.MainMenuScreen
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
        device.waitForIdle()
        composeRule.onNodeWithTag("settingsButton").performClick()
        composeRule.onNodeWithText("Manage Account").performClick()
    }

    @Test
    fun containsCorrectButtonsAndTextWhenNotSignedIn() {
        composeRule.onNodeWithTag("google_sign_in_button").assertIsDisplayed()
        composeRule.onNodeWithTag("sign_in_info").assertIsDisplayed()
    }

    @Test
    fun topAppBarAccountIsDisplayed() {
        composeRule.onNodeWithTag("topAppbarAccount").assertIsDisplayed()
    }

    @Test
    fun topAppBarCanBeClicked() {
        composeRule.onNodeWithTag("topAppbarAccount").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun signInResultsInCorrectLayout() {
        composeRule.onNodeWithTag("google_sign_in_button").performClick()
        device.wait(
            Until.findObject(By.textContains("Google")), 100000
        )
        val googleText = device.findObject(UiSelector().textContains(""))
        assert(googleText.exists())
    }



}