package com.github.freeman.bootcamp.auth

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*

import com.github.freeman.bootcamp.MainMenuScreen
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import junit.framework.Assert.assertNotNull
import org.hamcrest.core.Is.`is`
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
        composeRule.onNodeWithTag("SignInButton").performClick()
    }

    @Test
    fun deleteGoogleAccountResultsInCorrectMessage() {
        composeRule.onNodeWithTag("delete_button").performClick()
        composeRule.onNodeWithTag("sign_in_info").assertTextContains("Account deleted")
    }
}