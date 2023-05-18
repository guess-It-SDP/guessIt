package com.github.freeman.bootcamp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.GAME_OVER
import com.github.freeman.bootcamp.games.guessit.FinalScreen
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FinalActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            val context = LocalContext.current
            val dbRef = FirebaseUtilities.getGameDBRef(context)

            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FinalScreen(dbRef)
                }
            }
        }
    }

    @Test
    fun gameOverTextIsDisplayed() {
        composeRule.onNodeWithTag(GAME_OVER).assertIsDisplayed()
    }

    @Test
    fun endScoreboardIsDisplayed() {
        composeRule.onNodeWithTag("endScoreboard").assertIsDisplayed()
    }

    @Test
    fun backToMenuButtonIsClickable() {
        composeRule.onNodeWithTag("backToMenuButton").assertHasClickAction()
    }
}