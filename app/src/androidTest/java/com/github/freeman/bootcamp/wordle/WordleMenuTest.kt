package com.github.freeman.bootcamp.wordle

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.MainMenuScreen
import com.github.freeman.bootcamp.MainMenuTest
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.games.wordle.WordleMenuScreen
import com.github.freeman.bootcamp.testfunctions.TestCompanion
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.wordle.WordleGameActivityTest.Companion.createAndroidIntentComposeRule
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
    fun setTheContentBefore(){
        setWordleMenuScreen()
    }


    private fun setWordleMenuScreen() {
        composeRule.setContent {
                BootcampComposeTheme {
                    WordleMenuScreen()
                }
            }
        }


    @Test
    fun easyButtonIsDisplayedHasClickActionAndCorrectText(){
       TestCompanion.testButton(WordleMenu.Companion.Difficulty.EASY.prettyText,WordleGameActivity::class.java.name,WordleMenu.Companion.Difficulty.EASY.prettyText,composeRule)
    }

    @Test
    fun mediumButtonIsDisplayedHasClickActionAndCorrectText(){
        TestCompanion.testButton(WordleMenu.Companion.Difficulty.MEDIUM.prettyText,WordleGameActivity::class.java.name,WordleMenu.Companion.Difficulty.MEDIUM.prettyText,composeRule)
    }
    @Test
    fun hardButtonIsDisplayedHasClickActionAndCorrectText(){
        TestCompanion.testButton(WordleMenu.Companion.Difficulty.HARD.prettyText,WordleGameActivity::class.java.name,WordleMenu.Companion.Difficulty.HARD.prettyText,composeRule)
    }
    @Test
    fun veryHardButtonIsDisplayedHasClickActionAndCorrectText(){
        TestCompanion.testButton(WordleMenu.Companion.Difficulty.VERY_HARD.prettyText,WordleGameActivity::class.java.name,WordleMenu.Companion.Difficulty.VERY_HARD.prettyText,composeRule)
    }
    @Test
    fun veryVeryHardButtonIsDisplayedHasClickActionAndCorrectText(){
        TestCompanion.testButton(WordleMenu.Companion.Difficulty.VERY_VERY_HARD.prettyText,WordleGameActivity::class.java.name,WordleMenu.Companion.Difficulty.VERY_VERY_HARD.prettyText,composeRule)
    }

}