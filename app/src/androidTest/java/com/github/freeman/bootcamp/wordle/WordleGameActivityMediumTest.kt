package com.github.freeman.bootcamp.wordle

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity
import com.github.freeman.bootcamp.games.wordle.WordleGameState
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WordleGameActivityMediumTest {
    private val WORD_SIZE = 5
    private val NB_COLUMNS = 8

    @get:Rule
    val composeRule = createAndroidIntentComposeRule<WordleGameActivity> {
        Intent(it, WordleGameActivity::class.java).apply {
            putExtra("testing", true).putExtra(WordleMenu.Companion.Difficulty::class.simpleName,WordleMenu.Companion.Difficulty.MEDIUM.name)
        }
    }

    /**
     * Factory method to provide android specific implementation of createComposeRule, for a given
     * activity class type A that needs to be launched via an intent.
     *
     * @param intentFactory A lambda that provides a Context that can used to create an intent. A intent needs to be returned.
     */
    inline fun <A: ComponentActivity> createAndroidIntentComposeRule(intentFactory: (context: Context) -> Intent) : AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = intentFactory(context)

        return AndroidComposeTestRule(
            activityRule = ActivityScenarioRule(intent),
            activityProvider = { scenarioRule -> scenarioRule.getActivity() }
        )
    }

    @Test
    fun test(){
        WordleGameActivityTest.Companion.buttonIsDisplayed(composeRule)
    }

    /**
     * Gets the activity from a scenarioRule.
     *
     * https://androidx.tech/artifacts/compose.ui/ui-test-junit4/1.0.0-alpha11-source/androidx/compose/ui/test/junit4/AndroidComposeTestRule.kt.html
     */
    fun <A : ComponentActivity> ActivityScenarioRule<A>.getActivity(): A {
        var activity: A? = null

        scenario.onActivity { activity = it }

        return activity ?: throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
    }


    @Test
    fun buttonIsDisplayed(){
        WordleGameActivityTest.buttonIsDisplayed(composeRule)
    }

    @Test
    fun textFieldIsDisplayed() {
        WordleGameActivityTest.textFieldIsDisplayed(composeRule)
    }

    @Test
    fun gridIsDisplayed() {
        WordleGameActivityTest.gridIsDisplayed(composeRule, NB_COLUMNS * WORD_SIZE)
    }
    @Test
    fun addingHelloDoesntCrash() {
        WordleGameActivityTest.addingHelloDoesntCrash(composeRule, NB_COLUMNS * WORD_SIZE)
    }

    @Test
    fun adding6LettersDoesntCrash() {
        WordleGameActivityTest.adding6LettersDoesntCrash(composeRule, NB_COLUMNS * WORD_SIZE)
    }

    @Test
    fun gridColorisBlackAtStartOfTheGame() {
        WordleGameActivityTest.gridColorisBlackAtStartOfTheGame(composeRule, NB_COLUMNS * WORD_SIZE)
    }

    @Test
    fun submitLLLXLtoHelloAddRightColors() {
        WordleGameActivityTest.submitLLLXLtoHelloAddRightColors(composeRule)
    }
}