package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NB_ROUNDS
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NEXT
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.ROUNDS_SELECTION
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.categories
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.categorySize
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.selectedTopics
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.awaitility.Awaitility.await
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

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
    fun categoriesTextIsCorrect() {
        setGameOptionsScreen()
        for (category in categories) {
            composeRule.onNode(hasTestTag("categoryButtonText$category"), useUnmergedTree = true).assertTextContains(category)
        }
    }

    @Test
    fun nextButtonsIntentIsSent() {
        Intents.init()
        setGameOptionsScreen()
        categorySize = 10
        composeRule.onNode(hasTestTag("nextButton")).performClick()
        Intents.intended(IntentMatchers.hasComponent(TopicSelectionActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun nextButtonHasClickAction() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("nextButton")).assertHasClickAction()
    }

    @Test
    fun categoryButtonsHaveClickAction() {
        setGameOptionsScreen()
        for (category in categories) {
            composeRule.onNodeWithText(category).assertHasClickAction()
        }
    }

    @Test
    fun animalTopicsFetchedUponClick() {
        setGameOptionsScreen()
        assertTrue(selectedTopics.isEmpty())
        composeRule.onNodeWithText(categories[0]).performClick()

        // This step is necessary for the app to have enough time to fill the topics list
        composeRule.onNode(hasTestTag("nextButton")).performClick()
        await().atMost(90, TimeUnit.SECONDS).until { selectedTopics.isNotEmpty() }
        assertFalse(selectedTopics.isEmpty())
    }

    private fun setGameOptionsScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                GameOptionsScreen(dbref, gameId)
            }
        }
    }
}