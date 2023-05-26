package com.github.freeman.bootcamp.wordle

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.freeman.bootcamp.games.help.WordleRulesActivity
import com.github.freeman.bootcamp.games.help.WordleRulesActivity.Companion.TOPBAR_WORDLE_RULES_TEXT
import com.github.freeman.bootcamp.games.help.WordleRulesActivity.Companion.WORDLE_RULES_TITLE
import org.junit.Rule
import org.junit.Test

class WordleRulseActivityTest {
    /*
    @get:Rule
    val composeRule = createAndroidIntentComposeRule<WordleRulesActivity> {
        Intent(it, WordleRulesActivity::class.java).apply {
        }
    }

    @Test
    fun topBarIsDisplayedAndContainsCorrectText(){
        composeRule.onNodeWithTag("topAppbarWordleRules").assertIsDisplayed()
        composeRule.onNodeWithTag("topBarWordleRulesTitle").assertTextContains(TOPBAR_WORDLE_RULES_TEXT)
    }

    @Test
    fun wordleRulesAreDisplayed(){
        composeRule.onNodeWithTag("wordleRulesScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("how_to_play").assertIsDisplayed()
        composeRule.onNodeWithTag("wordleRulesText").assertIsDisplayed()
    }

    @Test
    fun wordleRulesAreDisplayedAndContainsCorrectText(){
        composeRule.onNodeWithTag("how_to_play").assertTextContains(WORDLE_RULES_TITLE)
    }


     */
}