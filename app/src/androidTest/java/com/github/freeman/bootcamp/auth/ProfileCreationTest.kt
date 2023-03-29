package com.github.freeman.bootcamp.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.FirebaseEmulator
import com.github.freeman.bootcamp.FirebaseSingletons
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileCreationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initScreen() {
        FirebaseEmulator.init()
        val database = FirebaseSingletons.database.get().database.getReference("Profiles")

        composeRule.setContent {
            BootcampComposeTheme {
                ProfileCreationScreen(database)
            }
        }
    }

    @Test
    fun profileCreationScreenIsDisplayed() {
        composeRule.onNodeWithTag("profileCreationScreen").assertIsDisplayed()
    }

    @Test
    fun usernameBarIsDisplayed() {
        composeRule.onNodeWithTag("usernameBar").assertIsDisplayed()
    }

    @Test
    fun usernameBarContainsCorrectLabel() {
        composeRule.onNodeWithTag("usernameLabel", true).assertTextContains("Username")
    }

    @Test
    fun usernameBarContainsCorrectPlaceholder() {
        composeRule.onNodeWithTag("usernamePlaceholder", true).assertTextContains("Choose a username")
    }

    @Test
    fun doneButtonHasClickAction() {
        composeRule.onNodeWithTag("usernameOkayButton").assertHasClickAction()
    }

    @Test
    fun profileCreationPreviewDisplaysProfileCreationScreen() {
        composeRule.onNodeWithTag("profileCreationScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("usernameBar").assertIsDisplayed()
    }

}