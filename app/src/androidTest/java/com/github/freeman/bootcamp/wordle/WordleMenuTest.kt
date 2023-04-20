package com.github.freeman.bootcamp.wordle

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.MainMenuScreen
import com.github.freeman.bootcamp.MainMenuTest
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenuActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenuActivityScreen
import com.github.freeman.bootcamp.testfunctions.TestCompanion
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.wordle.WordleGameActivityTest.Companion.createAndroidIntentComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordleMenuActivityTest {

    /*
    @get:Rule
    val composeRule = createAndroidIntentComposeRule<WordleMenuActivity> {
        Intent(it, WordleMenuActivity::class.java).apply {
        }
    }
    */
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setTheContentBefore(){
        setWordleMenuActivityScreen()
    }


    private fun setWordleMenuActivityScreen() {
        composeRule.setContent {
                BootcampComposeTheme {
                    WordleMenuActivityScreen()
                }
            }
        }


    @Test
    fun easyButtonIsDisplayedHasClickActionAndCorrectText(){
       TestCompanion.testButton(WordleMenuActivity.Companion.Difficulty.EASY.prettyText,WordleMenuActivity::class.java.name,WordleMenuActivity.Companion.Difficulty.EASY.prettyText,composeRule)
    }

    @Test
    fun easyButtonIsDisplayedHasClickActionAndCorrectText(){
        TestCompanion.testButton(WordleMenuActivity.Companion.Difficulty.EASY.prettyText,WordleMenuActivity::class.java.name,WordleMenuActivity.Companion.Difficulty.EASY.prettyText,composeRule)
    }
    @Test
    fun easyButtonIsDisplayedHasClickActionAndCorrectText(){
        TestCompanion.testButton(WordleMenuActivity.Companion.Difficulty.EASY.prettyText,WordleMenuActivity::class.java.name,WordleMenuActivity.Companion.Difficulty.EASY.prettyText,composeRule)
    }

}