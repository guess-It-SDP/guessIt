package com.github.freeman.bootcamp.wordle

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity
import com.github.freeman.bootcamp.games.wordle.WordleGameState
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.wordle.WordleGameActivityTest.Companion.assertBackgroundColor
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement


@LargeTest
@RunWith(AndroidJUnit4::class)
class WordleGameActivityVeryHardTest {

    private val WORD_SIZE = 5
    private val NB_COLUMNS = 8
    private val nbTiles = WORD_SIZE* NB_COLUMNS
    private lateinit var activityScenario: ActivityScenario<WordleGameActivity>

    @get:Rule
    val composeRule = AndroidComposeTestRule(EmptyTestRule()) {
        var activity: WordleGameActivity? = null
        activityScenario.onActivity { activity = it }
        checkNotNull(activity) { "Activity didn't launch" }
    }

    class EmptyTestRule : TestRule {
        override fun apply(base: Statement, description: Description) = base
    }

    @Test
    fun test() {
        setupSomethingFirst()
        ActivityScenario.launch<WordleGameActivity>(Intent(
            ApplicationProvider.getApplicationContext(), WordleGameActivity::class.java
        ).apply {
            putExtra("testing", true).putExtra(WordleMenu.Companion.Difficulty::class.simpleName,WordleMenu.Companion.Difficulty.VERY_HARD.name)
        }).use {

            composeRule.onNodeWithTag("submitWordButton").assertExists()
            composeRule.onNode(hasText("Enter a 5 letters word to submit")).assertExists()
            composeRule.onNode(hasTestTag("wordle_tile_grid")).assertExists()
            for (i in 0 until WORD_SIZE * NB_COLUMNS) {
                composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString())).assertExists()
            }
            composeRule.onNode(hasText("Enter a 5 letters word to submit"))
                .performTextInput("hello")
            Espresso.closeSoftKeyboard()

            composeRule.onNodeWithTag("submitWordButton").performClick()
            for (i in 0 until WORD_SIZE * NB_COLUMNS) {
                composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString())).assertIsDisplayed()
            }

        }
    }

    @Test
    fun test2() {
        setupSomethingFirst()
        ActivityScenario.launch<WordleGameActivity>(Intent(
            ApplicationProvider.getApplicationContext(), WordleGameActivity::class.java
        ).apply {
            putExtra("testing", true).putExtra(WordleMenu.Companion.Difficulty::class.simpleName,WordleMenu.Companion.Difficulty.VERY_HARD.name)
        }).use {

            composeRule.onNode(hasText("Enter a 5 letters word to submit")).performTextInput("helloo")
            Espresso.closeSoftKeyboard()
            composeRule.onNodeWithTag("submitWordButton").performClick()
            for (i in 0 until nbTiles) {
                composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString())).assertIsDisplayed()
            }
        }
    }

    @Test
    fun test3() {
        setupSomethingFirst()
        ActivityScenario.launch<WordleGameActivity>(Intent(
            ApplicationProvider.getApplicationContext(), WordleGameActivity::class.java
        ).apply {
            putExtra("testing", true).putExtra(WordleMenu.Companion.Difficulty::class.simpleName,WordleMenu.Companion.Difficulty.VERY_HARD.name)
        }).use {

            composeRule.onNode(hasTestTag("wordle_tile_grid")).assertExists()
            for (i in 0 until nbTiles) {
                composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString()))
                    .assertBackgroundColor(Color(WordleGameState.TileState.EMPTY.argb))
            }
        }
    }


    private fun setupSomethingFirst() {
    }
}