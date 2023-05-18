package com.github.freeman.bootcamp

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.ShareRecapActivity.Companion.SHARE_RECAP_TITLE
import com.github.freeman.bootcamp.games.guessit.ShareRecapScreen
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShareRecapTest {

    @get:Rule
    val composeRule = createComposeRule()


    @Before
    fun init() {
        FirebaseEmulator.init()
        composeRule.setContent {
            BootcampComposeTheme {
                ShareRecapScreen("test_game_id")
            }
        }
    }

    @Test
    fun shareVideoScreenIsDisplayed() {
        composeRule.onNodeWithTag("shareRecapScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("shareRecapTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("recapPreview").assertIsDisplayed()
        composeRule.onNodeWithTag("shareRecapButton").assertIsDisplayed()
    }

    @Test
    fun shareVideoScreenContainsCorrectTexts() {
        composeRule.onNodeWithTag("shareRecapTitle").assertTextContains(SHARE_RECAP_TITLE)
    }

    @Test
    fun shareButtonHasClickedAction() {
        composeRule.onNodeWithTag("shareRecapButton").assertHasClickAction()
    }

}