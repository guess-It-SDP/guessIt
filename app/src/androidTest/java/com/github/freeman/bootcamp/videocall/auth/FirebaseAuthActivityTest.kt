package com.github.freeman.bootcamp.videocall.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.github.freeman.bootcamp.MainMenuScreen
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.CANCEL_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.DELETE_BUTTON
import com.github.freeman.bootcamp.auth.WarningDeletion
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirebaseAuthActivityTest {
    private lateinit var device: UiDevice

    @get:Rule
    val composeRule = createComposeRule()

    private fun startMainActivityFromHomeScreen() {
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
        startMainActivityFromHomeScreen()
        composeRule.onNodeWithTag("google_sign_in_button").assertIsDisplayed()
        composeRule.onNodeWithTag("sign_in_info").assertIsDisplayed()
    }

    @Test
    fun topAppBarAccountIsDisplayed() {
        startMainActivityFromHomeScreen()
        composeRule.onNodeWithTag("topAppbarAccount").assertIsDisplayed()
    }

    @Test
    fun topAppBarCanBeClicked() {
        startMainActivityFromHomeScreen()
        composeRule.onNodeWithTag("topAppbarAccount").performClick()
        composeRule.waitForIdle()
    }

//    @Test
//    fun signInResultsInCorrectLayout() {
//        startMainActivityFromHomeScreen()
//        composeRule.onNodeWithTag("google_sign_in_button").performClick()
//        device.wait(
//            Until.findObject(By.textContains("Google")), 100000
//        )
//        val googleText = device.findObject(UiSelector().textContains(""))
//        assert(googleText.exists())
//    }

    // As the button for deletion only appears when authenticated with google,
    // we can't test it from the authentication screen
    private fun initDeletionWarning() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)

        val signInInfo = mutableStateOf("")
        val currentUser = mutableStateOf( FirebaseAuth.getInstance().currentUser)
        val alertOpen = mutableStateOf(true)

        composeRule.setContent {
            WarningDeletion(signInInfo, currentUser, alertOpen)
        }
        device.waitForIdle()
    }

    @Test
    fun deletionWarningIsDisplayed() {
        initDeletionWarning()
        composeRule.onNodeWithTag("deletionAlertDialog").assertIsDisplayed()
        composeRule.onNodeWithTag("deleteButton").assertIsDisplayed()
        composeRule.onNodeWithTag("cancelButton").assertIsDisplayed()
        composeRule.onNodeWithTag("deletionAlertTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("deletionAlertText").assertIsDisplayed()
    }

    @Test
    fun deletionWarningContainsCorrectText() {
        initDeletionWarning()
        composeRule.onNodeWithTag("deletionAlertTitle").assertTextContains(FirebaseAuthActivity.DELETION_WARNING_TITLE)
        composeRule.onNodeWithTag("deletionAlertText").assertTextContains(FirebaseAuthActivity.DELETION_WARNING_TEXT)
    }

    @Test
    fun continueDeletionButtonContainsCorrectText() {
        initDeletionWarning()
        composeRule.onNodeWithTag("deleteButton").assertHasClickAction()
        composeRule.onNodeWithTag("deleteButton").assertTextContains(DELETE_BUTTON)
    }

    @Test
    fun cancelDeletionButtonContainsCorrectText() {
        initDeletionWarning()
        composeRule.onNodeWithTag("cancelButton").assertTextContains(CANCEL_BUTTON)
        composeRule.onNodeWithTag("cancelButton").assertHasClickAction()
    }

}