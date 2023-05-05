package com.github.freeman.bootcamp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.freeman.bootcamp.SettingsActivity.Companion.SETTINGS_TITLE
import com.github.freeman.bootcamp.games.guessit.StatsActivity
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.BEST_SCORE
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.NB_CORRECT_GUESSES
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.NB_GAMES_PLAYED
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.PCT_MAKE_GUESS
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.PCT_QUICKEST_GUESSER
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.PCT_RIGHT_GUESSSER
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.STATS_TITLE
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingScreen
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatsTest {
    private lateinit var device: UiDevice

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun startStatsActivityFromHomeScreen() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)

        composeRule.setContent {
            MainMenuScreen()
        }
        device.waitForIdle()
        composeRule.onNodeWithTag("settingsButton").performClick()
        composeRule.onNodeWithText("Game Stats").performClick()
    }

    @Test
    fun statsScreenIsDisplayed() {
        composeRule.onNodeWithTag("statsScreen").assertIsDisplayed()
    }

    @Test
    fun topBarIsDisplayedAndContainsCorrectText() {
        composeRule.onNodeWithTag("topAppbarStats").assertIsDisplayed()
        composeRule.onNodeWithTag("topBarStatsText").assertIsDisplayed()
        composeRule.onNodeWithTag("topBarStatsText").assertTextContains(STATS_TITLE)
    }

    @Test
    fun allStatsAreDisplayed() {
        composeRule.onNodeWithTag("nbGames title").assertIsDisplayed()
        composeRule.onNodeWithTag("bestScore title").assertIsDisplayed()
        composeRule.onNodeWithTag("nbCorrectGs title").assertIsDisplayed()
        composeRule.onNodeWithTag("pctFirstGuesser title").assertIsDisplayed()
        composeRule.onNodeWithTag("pctRightGuesser title").assertIsDisplayed()
        composeRule.onNodeWithTag("pctSomebodyGuessed title").assertIsDisplayed()
        composeRule.onNodeWithTag("nbGames").assertIsDisplayed()
        composeRule.onNodeWithTag("bestScore").assertIsDisplayed()
        composeRule.onNodeWithTag("nbCorrectGs").assertIsDisplayed()
        composeRule.onNodeWithTag("pctFirstGuesser").assertIsDisplayed()
        composeRule.onNodeWithTag("pctRightGuesser").assertIsDisplayed()
        composeRule.onNodeWithTag("pctSomebodyGuessed").assertIsDisplayed()
    }

    @Test
    fun allStatsContainsCorrectText() {
        composeRule.onNodeWithTag("nbGames title").assertTextContains(NB_GAMES_PLAYED, substring = true)
        composeRule.onNodeWithTag("bestScore title").assertTextContains(BEST_SCORE, substring = true)
        composeRule.onNodeWithTag("nbCorrectGs title").assertTextContains(NB_CORRECT_GUESSES, substring = true)
        composeRule.onNodeWithTag("pctFirstGuesser title").assertTextContains(PCT_QUICKEST_GUESSER, substring = true)
        composeRule.onNodeWithTag("pctRightGuesser title").assertTextContains(PCT_RIGHT_GUESSSER, substring = true)
        composeRule.onNodeWithTag("pctSomebodyGuessed title").assertTextContains(PCT_MAKE_GUESS, substring = true)
    }

    @Test
    fun percentageStatsContainsSymbol() {
        composeRule.onNodeWithTag("pctFirstGuesser").assertTextContains("%", substring = true)
        composeRule.onNodeWithTag("pctRightGuesser").assertTextContains("%", substring = true)
        composeRule.onNodeWithTag("pctSomebodyGuessed").assertTextContains("%", substring = true)
    }

}