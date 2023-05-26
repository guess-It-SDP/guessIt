package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.help.CreditsActivity.Companion.TOPBAR_CREDITS_TEXT
import com.github.freeman.bootcamp.games.help.GuessItRulesActivity
import com.github.freeman.bootcamp.games.help.HelpActivity
import com.github.freeman.bootcamp.games.help.HelpActivity.Companion.CREDITS_BUTTON
import com.github.freeman.bootcamp.games.help.HelpActivity.Companion.OFFLINE_RULES
import com.github.freeman.bootcamp.games.help.HelpActivity.Companion.ONLINE_RULES
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelpTests {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initFromMain() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }

        composeRule.onNodeWithTag("settingsButton").performClick()
        composeRule.onNodeWithText("Help").performClick()
    }

    @Test
    fun helpScreenIsDisplayed() {
        composeRule.onNodeWithTag("topAppbarHelp").assertIsDisplayed()
        composeRule.onNodeWithTag("helpScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("helpText").assertIsDisplayed()
        composeRule.onNodeWithTag("creditsButton").assertIsDisplayed()
        composeRule.onNodeWithTag("guessItRulesButton").assertIsDisplayed()
        composeRule.onNodeWithTag("guessItRulesButton").assertIsDisplayed()
    }

    @Test
    fun helpScreenTextIsCorrect() {
        composeRule.onNodeWithTag("topBarHelpTitle").assertTextContains(HelpActivity.TOPBAR_HELP_TEXT)
        composeRule.onNodeWithTag("helpText").assertTextContains(HelpActivity.HELP_TITLE)
        composeRule.onNodeWithTag("creditsButton").assertTextContains(CREDITS_BUTTON)
        composeRule.onNodeWithTag("guessItRulesButton").assertTextContains(ONLINE_RULES)
        composeRule.onNodeWithTag("wordleRulesButton").assertTextContains(OFFLINE_RULES)
    }

    @Test
    fun creditsScreenIsDisplayed() {
        composeRule.onNodeWithTag("creditsButton").performClick()

        composeRule.onNodeWithTag("topAppbarCredits").assertIsDisplayed()
        composeRule.onNodeWithTag("creditsScreen").assertIsDisplayed()
    }

    @Test
    fun creditsScreenContainsCorrectTexts() {
        composeRule.onNodeWithTag("creditsButton").performClick()

        composeRule.onNodeWithTag("topAppbarCreditsTitle").assertTextContains(TOPBAR_CREDITS_TEXT)
        composeRule.onNodeWithTag("creditsScreen").assertIsDisplayed()

        composeRule.onNodeWithTag("This ").assertTextContains("This ", substring = true)
        composeRule.onNodeWithTag("Devel").assertTextContains("Devel", substring = true)
        composeRule.onNodeWithTag("Micha").assertTextContains("Micha", substring = true)
        composeRule.onNodeWithTag("Danny").assertTextContains("Danny", substring = true)
        composeRule.onNodeWithTag("Clara").assertTextContains("Clara", substring = true)
        composeRule.onNodeWithTag("David").assertTextContains("David", substring = true)
        composeRule.onNodeWithTag("Paul ").assertTextContains("Paul ", substring = true)
        composeRule.onNodeWithTag("Profe").assertTextContains("Profe", substring = true)
        composeRule.onNodeWithTag("Georg").assertTextContains("Georg", substring = true)
        composeRule.onNodeWithTag("Super").assertTextContains("Super", substring = true)
        composeRule.onNodeWithTag("Can C").assertTextContains("Can C", substring = true)
        composeRule.onNodeWithTag("Mathi").assertTextContains("Mathi", substring = true)
    }

    @Test
    fun guessItScreenIsDisplayed() {
        composeRule.onNodeWithTag("guessItRulesButton").performClick()

        composeRule.onNodeWithTag("topAppbarGuessItRules").assertIsDisplayed()
        composeRule.onNodeWithTag("guessItRulesScreen").assertIsDisplayed()
    }

    @Test
    fun guessItScreenContainsCorrectTexts() {
        composeRule.onNodeWithTag("guessItRulesButton").performClick()

        composeRule.onNodeWithTag("topBarGuessItRulesTitle").assertTextContains(GuessItRulesActivity.TOPBAR_GUESSIT_RULES_TEXT)
        composeRule.onNodeWithTag("how_to_play").assertTextContains(GuessItRulesActivity.GUESSIT_RULES_TITLE)

    }




}