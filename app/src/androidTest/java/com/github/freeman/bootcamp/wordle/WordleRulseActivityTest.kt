package com.github.freeman.bootcamp.wordle

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.freeman.bootcamp.games.help.TopAppbarWordleRules
import com.github.freeman.bootcamp.games.help.WordleRulesActivity.Companion.TOPBAR_WORDLE_RULES_TEXT
import com.github.freeman.bootcamp.games.help.WordleRulesActivity.Companion.WORDLE_RULES_TITLE
import com.github.freeman.bootcamp.games.help.WordleRulesDisplay
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WordleRulseActivityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initWordleRules() {
        composeRule.setContent {
            BootcampComposeTheme {
                Surface {
                    Column {
                        TopAppbarWordleRules()
                        WordleRulesDisplay()
                    }
                }
            }

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

}