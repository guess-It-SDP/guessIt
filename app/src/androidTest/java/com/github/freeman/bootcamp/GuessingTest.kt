package com.github.freeman.bootcamp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.SCREEN_TEXT
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

        composeRule.setContent {
            val context = LocalContext.current
            val guessGameId = context.getString(R.string.test_game_id)
            val database = FirebaseSingletons.database.get().database.reference
                .child(context.getString(R.string.games_path))
                .child(guessGameId)
                .child(context.getString(R.string.guesses_path))

            BootcampComposeTheme {
                GuessingScreen(database, context = context)
            }
        }
    }

    @Test
    fun guessingScreenIsDisplayed() {
        composeRule.onNodeWithTag("guessingScreen").assertIsDisplayed()
    }

    @Test
    fun guessingScreenContainsCorrectText() {
        composeRule.onNodeWithTag("guessText").assertTextContains(SCREEN_TEXT)
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
        composeRule.onNodeWithTag("guessText").assertTextContains(SCREEN_TEXT)
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