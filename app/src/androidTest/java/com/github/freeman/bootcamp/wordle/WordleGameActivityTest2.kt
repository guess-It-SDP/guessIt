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
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WordleGameActivityTest2 {
    @get:Rule
    val composeRule = createAndroidIntentComposeRule<WordleGameActivity> {
        Intent(it, WordleGameActivity::class.java).apply {
            putExtra("testing", false)
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
    fun buttonIsDisplayed() {
        composeRule.onNode(hasText("Submit word")).assertIsDisplayed()
    }


    @Test
    fun textFieldIsDisplayed() {
        composeRule.onNode(hasText("Enter a 5 letters word to submit")).assertIsDisplayed()
    }

    @Test
    fun gridIsDisplayed() {
        composeRule.onNode(hasTestTag("wordle_tile_grid")).assertIsDisplayed()
        for (i in 0 until 40) {
            composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString())).assertIsDisplayed()
        }
    }
    @Test
    fun addingHelloDoesntCrash() {
        composeRule.onNode(hasText("Enter a 5 letters word to submit")).performTextInput("hello")
        Espresso.closeSoftKeyboard()
        composeRule.onNode(hasText("Submit word")).performClick()
        for (i in 0 until 40) {
            composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString())).assertIsDisplayed()
        }
    }

    @Test
    fun adding6LettersDoesntCrash() {
        composeRule.onNode(hasText("Enter a 5 letters word to submit")).performTextInput("helloo")
        Espresso.closeSoftKeyboard()
        composeRule.onNode(hasText("Submit word")).performClick()
        for (i in 0 until 40) {
            composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString())).assertIsDisplayed()
        }
    }

    @Test
    fun gridColorisBlackAtStartOfTheGame() {
        composeRule.onNode(hasTestTag("wordle_tile_grid")).assertIsDisplayed()
        for (i in 0 until 40) {
            composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString()))
                .assertBackgroundColor(Color(WordleGameState.TileState.EMPTY.argb))
        }
    }


    fun SemanticsNodeInteraction.assertBackgroundColor(expectedBackground: Color) {
        val capturedName = captureToImage().colorSpace.name
        TestCase.assertEquals(expectedBackground.colorSpace.name, capturedName)
    }


}