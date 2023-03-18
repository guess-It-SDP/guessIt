package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NB_ROUNDS
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NEXT
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.ROUNDS_SELECTION
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameOptionsActivityTest {
    private val gameId = "TestGameId"
    private val dbref = Firebase.database.getReference("Games/$gameId")

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun gameOptionsScreenIsDisplayed() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("gameOptionsScreen")).assertIsDisplayed()
    }

    @Test
    fun screenHasRoundsTitle() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("roundsSelection")).assertTextContains(ROUNDS_SELECTION)
    }

    @Test
    fun roundNumbersAreCorrect() {
        setGameOptionsScreen()
        for (nb in NB_ROUNDS) {
            composeRule.onNode(hasTestTag("radioButtonText$nb"))
            composeRule.onNode(hasTestTag("radioButtonText$nb")).assertTextContains(nb)
        }
    }

    @Test
    fun nextButtonTextIsCorrect() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("nextButton")).assertTextContains(NEXT)
    }

    @Test
    fun nextButtonHasClickAction() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("nextButton")).assertHasClickAction()
    }

    @Test
    fun nextButtonsIntentIsSent() {
        Intents.init()
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("nextButton")).performClick()
        Intents.intended(IntentMatchers.hasComponent(TopicSelectionActivity::class.java.name))
        Intents.release()
    }

    private fun setGameOptionsScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                GameOptionsScreen(dbref, gameId)
            }
        }
    }
}