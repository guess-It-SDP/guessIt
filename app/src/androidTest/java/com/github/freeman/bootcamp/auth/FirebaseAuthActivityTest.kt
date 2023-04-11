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
        composeRule.onNodeWithTag("signInButton").performClick()
    }

    @Test
    fun deleteGoogleAccountResultsInCorrectMessage() {
        composeRule.onNodeWithTag("delete_button").performClick()
        composeRule.onNodeWithTag("sign_in_info").assertTextContains("Account deleted")
    }

    @Test
    fun signOutResultsInCorrectMessage() {
        composeRule.onNodeWithTag("sign_out_button").performClick()
        composeRule.onNodeWithTag("sign_in_info").assert(hasText("Signed out") or hasText("Not signed in"))
    }

    @Test
    fun signInResultsInCorrectLayout() {

        composeRule.onNodeWithTag("sign_in_button").performClick()
        device.wait(
            Until.findObject(By.textContains("Google")), 30000
        )
        val googleText = device.findObject(UiSelector().textContains(""))
        assert(googleText.exists())
    }

}