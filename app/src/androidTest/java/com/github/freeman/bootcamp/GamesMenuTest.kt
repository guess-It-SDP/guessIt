package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GamesMenuTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initFromMain() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
        composeRule.onNodeWithText("Play Game").performClick()
    }

    @Test
    fun gamesMenuScreenIsDisplayed() {
        composeRule.onNodeWithTag("gamesMenuScreen").assertIsDisplayed()
    }

    @Test
    fun gamesMenuTextIsDisplayedAndIsCorrect() {
        composeRule.onNodeWithTag("gamesMenuText").assertIsDisplayed()
        composeRule.onNodeWithTag("gamesMenuText").assertTextContains(GamesMenuActivity.GAMES_MENU_TEXT)
    }

    @Test
    fun guessItButton() {
        composeRule.onNodeWithTag("guessItButton").assertIsDisplayed()
        composeRule.onNodeWithTag("guessItButton").assertTextContains("Guess It!")
        composeRule.onNodeWithTag("guessItButton").assertHasClickAction()

        Intents.init()
        composeRule.onNodeWithTag("guessItButton").performClick()
        Intents.intended(IntentMatchers.hasComponent(CreateJoinActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun wordleButton() {
        composeRule.onNodeWithTag("wordleButton").assertIsDisplayed()
        composeRule.onNodeWithTag("wordleButton").assertTextContains("Wordle")
        composeRule.onNodeWithTag("wordleButton").assertHasClickAction()

        Intents.init()
        composeRule.onNodeWithTag("wordleButton").performClick()
        Intents.intended(IntentMatchers.hasComponent(WordleMenu::class.java.name))
        Intents.release()
    }

}