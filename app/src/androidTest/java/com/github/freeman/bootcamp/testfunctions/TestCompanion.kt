package com.github.freeman.bootcamp.testfunctions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity

object TestCompanion {
    /**
     * Test is a button is displayed, has the right text, is clickable and sends the correct intents
     */
    fun testButton(testTag: String, activityClassName: String, text: String,composeRule: ComposeContentTestRule) {
        node(testTag, composeRule).assertIsDisplayed().assertHasClickAction().assertTextContains(text)
        intentIsSend(testTag, activityClassName,composeRule)
    }

    /**
     * Test that an intend is sent when clicking on a button with given test tag
     */
    private fun intentIsSend(testTag: String, activityClassName: String,composeRule: ComposeContentTestRule) {
        Intents.init()
        node(testTag,composeRule).performClick()
        Intents.intended(IntentMatchers.hasComponent(activityClassName))
        Intents.release()
    }

    /**
     * Return a node from a test Tag
     */
    private fun node(testTag: String,composeRule: ComposeContentTestRule) : SemanticsNodeInteraction {
        return composeRule.onNodeWithTag(testTag)
    }


}