package com.github.freeman.bootcamp.wordle

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.freeman.bootcamp.games.wordle.WordleRulesActivity
import com.github.freeman.bootcamp.wordle.WordleGameActivityTest.Companion.createAndroidIntentComposeRule
import org.junit.Rule
import org.junit.Test

class WordleRulseActivityTest {
    @get:Rule
    val composeRule = createAndroidIntentComposeRule<WordleRulesActivity> {
        Intent(it, WordleRulesActivity::class.java).apply {
        }
    }

    @Test
    fun textIsDisplayed(){
        composeRule.onNodeWithTag(WordleRulesActivity.WORDLE_RULE_ACTIVITY_TEST_TAG).assertIsDisplayed()
    }
}