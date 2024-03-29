package com.github.freeman.bootcamp

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity.Companion.SELECT_TOPIC
import com.github.freeman.bootcamp.games.guessit.TopicSelectionScreen
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class TopicSelectionActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun topicSelectionIsDisplayed() {
        setTopicSelectionScreen()
        composeRule.onNode(hasTestTag("topicSelectionScreen")).assertIsDisplayed()
    }

    @Test
    fun screenHasSelectTopicTitle() {
        setTopicSelectionScreen()
        composeRule.onNode(hasTestTag("topicSelection")).assertTextContains(SELECT_TOPIC)
    }

    @Test
    fun refreshButtonIsDisplayed() {
        setTopicSelectionScreen()
        composeRule.onNodeWithTag("refreshButton").assertIsDisplayed()
    }

    @Test
    fun refreshButtonHasClickAction() {
        setTopicSelectionScreen()
        composeRule.onNodeWithTag("refreshButton").performClick()
    }


    @Test
    fun topicButtonHasClickAction() {
        setTopicSelectionScreen()
        for (id in 1..3) {
            composeRule.onNode(hasTestTag("topicButton$id")).assertHasClickAction()
        }
    }

    @Test
    fun nextButtonsIntentIsSent() {
        Intents.init()
        setTopicSelectionScreen()
        val id = Random.nextInt(1, 4)
        composeRule.onNode(hasTestTag("topicButton$id")).performClick()
        Intents.intended(IntentMatchers.hasComponent(DrawingActivity::class.java.name))
        Intents.release()
    }

    private fun setTopicSelectionScreen() {
        composeRule.setContent {
            val context = LocalContext.current
            val gameId = context.getString(R.string.test_game_id)
            val dbref = getGameDBRef(context)
            BootcampComposeTheme {
                TopicSelectionScreen(dbref, gameId)
            }
        }
    }
}