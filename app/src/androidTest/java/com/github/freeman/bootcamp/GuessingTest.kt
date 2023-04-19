package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.answer
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingScreen
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuessingTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initScreenWithDatabase() {
        FirebaseEmulator.init()
        val guessGameId = "GameTestGuessesId"
        val database = FirebaseSingletons.database.get().database.getReference("games/$guessGameId/guesses")

        composeRule.setContent {
            BootcampComposeTheme {
                GuessingScreen(database)
            }
        }
    }

    @Test
    fun guessingScreenIsDisplayed() {
        composeRule.onNodeWithTag("guessingScreen").assertIsDisplayed()
    }

    @Test
    fun guessingScreenContainsCorrectText() {
        composeRule.onNodeWithTag("guessText").assertTextContains("Your turn to guess!")
    }

    @Test
    fun guessesListIsDisplayed() {
        composeRule.onNodeWithTag("guessesList").assertIsDisplayed()
    }

    @Test
    fun guessingBarIsDisplayed() {
        composeRule.onNodeWithTag("guessingBar").assertIsDisplayed()
    }

    @Test
    fun guessingPreviewDisplaysGuessingScreen() {
        composeRule.onNodeWithTag("guessingScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("guessText").assertTextContains("Your turn to guess!")
        composeRule.onNodeWithTag("guessesList").assertIsDisplayed()
        composeRule.onNodeWithTag("guessingBar").assertIsDisplayed()
    }

    @Test
    fun guessIsDisplayedInGuessingList() {
        composeRule.onNode(hasSetTextAction()).performTextInput("House")
        composeRule.onNodeWithTag("guessButton").performClick()
    }

    @Test
    fun popupIsDisplayedUponGuessingCorrectly() {
        composeRule.onNode(hasSetTextAction()).performTextInput(answer)
        composeRule.onNodeWithTag("guessButton").performClick()
        composeRule.onNodeWithTag("popUpScreen").assertIsDisplayed()
    }
}