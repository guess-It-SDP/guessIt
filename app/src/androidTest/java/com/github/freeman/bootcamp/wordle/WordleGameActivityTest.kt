package com.github.freeman.bootcamp.wordle


import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordleGameActivityTest {
    @get:Rule
    val composeRule = createAndroidIntentComposeRule<WordleGameActivity> {
        Intent(it, WordleGameActivity::class.java).apply {
            putExtra("testing", true)
        }
    }

    /**
     * Factory method to provide android specific implementation of createComposeRule, for a given
     * activity class type A that needs to be launched via an intent.
     * https://stackoverflow.com/questions/68267861/add-intent-extras-in-compose-ui-test
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
        Espresso.closeSoftKeyboard();
        composeRule.onNode(hasText("Submit word")).performClick()
        for (i in 0 until 40) {
            composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString())).assertIsDisplayed()
        }
    }

    @Test
    fun adding6LettersDoesntCrash() {
        composeRule.onNode(hasText("Enter a 5 letters word to submit")).performTextInput("helloo")
        Espresso.closeSoftKeyboard();
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

    @Test
    fun submitLLLXLtoHelloAddRightColors() {
        composeRule.onNode(hasText("Enter a 5 letters word to submit")).performTextInput("LLLXL")
        Espresso.closeSoftKeyboard();
        composeRule.onNode(hasText("Submit word")).performClick()
        composeRule.onNode(hasTestTag("wordle_tile_id_" + 0.toString()))
            .assertBackgroundColor(Color(WordleGameState.TileState.WRONG_SPOT.argb))
        composeRule.onNode(hasTestTag("wordle_tile_id_" + 1.toString()))
            .assertBackgroundColor(Color(WordleGameState.TileState.INCORRECT.argb))
        composeRule.onNode(hasTestTag("wordle_tile_id_" + 2.toString()))
            .assertBackgroundColor(Color(WordleGameState.TileState.CORRECT.argb))
        composeRule.onNode(hasTestTag("wordle_tile_id_" + 3.toString()))
            .assertBackgroundColor(Color(WordleGameState.TileState.INCORRECT.argb))
        composeRule.onNode(hasTestTag("wordle_tile_id_" + 4.toString()))
            .assertBackgroundColor(Color(WordleGameState.TileState.INCORRECT.argb))
    }

    /**
     * allow to compare colors
     * https://stackoverflow.com/questions/70682864/android-jetpack-compose-how-to-test-background-color
     */
    fun SemanticsNodeInteraction.assertBackgroundColor(expectedBackground: Color) {
        val capturedName = captureToImage().colorSpace.name
        assertEquals(expectedBackground.colorSpace.name, capturedName)
    }


}


