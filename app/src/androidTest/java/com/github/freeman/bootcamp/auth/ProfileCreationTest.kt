package com.github.freeman.bootcamp.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.FirebaseEmulator
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileCreationTest {
    @get:Rule
    val composeRule = createComposeRule()

    fun initScreen() {
        FirebaseEmulator.init()
        val database = FirebaseSingletons.database.get().database.getReference("Profiles")
        val storage = FirebaseSingletons.storage.get().storage.reference

        composeRule.setContent {
            BootcampComposeTheme {
                ProfileCreationScreen(database, storage)
            }
        }
    }

    @Test
    fun profileCreationScreenIsDisplayed() {
        initScreen()
        composeRule.onNodeWithTag("profileCreationScreen").assertIsDisplayed()
    }

    @Test
    fun usernameBarIsDisplayed() {
        initScreen()
        composeRule.onNodeWithTag("usernameBar").assertIsDisplayed()
    }

    @Test
    fun usernameLabelIsDisplayed() {
        initScreen()
        composeRule.onNode(hasTestTag("usernameLabel"), true).assertIsDisplayed()
    }

    @Test
    fun usernameBarContainsCorrectLabel() {
        initScreen()
        composeRule.onNode(hasTestTag("usernameLabel"), true).assertTextContains("Username")
    }

    @Test
    fun usernamePlaceholderIsDisplayed() {
        initScreen()
        composeRule.onNodeWithTag("usernamePlaceholder", true).assertIsDisplayed()
    }

    @Test
    fun usernameBarContainsCorrectPlaceholder() {
        initScreen()
        composeRule.onNodeWithTag("usernamePlaceholder", true).assertTextContains("Choose a username")
    }

    @Test
    fun doneButtonHasClickAction() {
        initScreen()
        composeRule.onNodeWithTag("usernameOkayButton").assertHasClickAction()
    }

    @Test
    fun doneButtonHasCorrectDescription() {
        initScreen()
        composeRule.onNodeWithTag("usernameOkayButton").assertContentDescriptionContains( "okIconForUsername")
    }


}