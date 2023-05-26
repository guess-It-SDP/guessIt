package com.github.freeman.bootcamp.wordle

import androidx.compose.material.Surface
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.help.WordleRulesActivity
import com.github.freeman.bootcamp.games.wordle.TopAppbarWordleMenu
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.games.wordle.WordleMenuScreen
import com.github.freeman.bootcamp.testfunctions.TestCompanion
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordleMenuTest {

    /*
    @get:Rule
    val composeRule = createAndroidIntentComposeRule<WordleMenu> {
        Intent(it, WordleMenu::class.java).apply {
        }
    }
    */
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setTheContentBefore() {
        setWordleMenuScreen()
    }


    private fun setWordleMenuScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                Surface {
                    TopAppbarWordleMenu()
                    WordleMenuScreen()
                }
            }
        }
    }

    /*
    @Test
    fun easyButtonIsDisplayedHasClickActionAndCorrectTextAndSendIntent() {
        TestCompanion.testButton(
            WordleMenu.Companion.Difficulty.EASY.prettyText,
            WordleGameActivity::class.java.name,
            WordleMenu.Companion.Difficulty.EASY.prettyText + " (letters)",
            composeRule
        )
    }

    @Test
    fun mediumButtonIsDisplayedHasClickActionAndCorrectTextAndSendIntent() {
        TestCompanion.testButton(
            WordleMenu.Companion.Difficulty.MEDIUM.prettyText,
            WordleGameActivity::class.java.name,
            WordleMenu.Companion.Difficulty.MEDIUM.prettyText + " (letters)",
            composeRule
        )
    }

    @Test
    fun hardButtonIsDisplayedHasClickActionAndCorrectTextAndSendIntent() {
        TestCompanion.testButton(
            WordleMenu.Companion.Difficulty.HARD.prettyText,
            WordleGameActivity::class.java.name,
            WordleMenu.Companion.Difficulty.HARD.prettyText + " (words only)",
            composeRule
        )
    }

    @Test
    fun veryHardButtonIsDisplayedHasClickActionAndCorrectTextAndSendIntent() {
        TestCompanion.testButton(
            WordleMenu.Companion.Difficulty.VERY_HARD.prettyText,
            WordleGameActivity::class.java.name,
            WordleMenu.Companion.Difficulty.VERY_HARD.prettyText + " (words only)",
            composeRule
        )
    }

    @Test
    fun veryVeryHardButtonIsDisplayedHasClickActionAndCorrectTextAndSendIntent() {
        TestCompanion.testButton(
            WordleMenu.Companion.Difficulty.VERY_VERY_HARD.prettyText,
            WordleGameActivity::class.java.name,
            WordleMenu.Companion.Difficulty.VERY_VERY_HARD.prettyText + " (words only)",
            composeRule
        )
    }
    */
}