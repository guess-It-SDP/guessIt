package com.github.freeman.bootcamp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CorrectAnswerTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val guesser = "ItsMe"
    private val guess = "Flower"

    @Before
    fun initScreen() {
        FirebaseEmulator.init()

        val guess = Guess(guesser, guess)

        composeRule.setContent {
            BootcampComposeTheme {
                CorrectAnswerScreen(gs = guess)
            }
        }
    }

    @Test
    fun correctAnswerScreenIsDisplayed() {
        composeRule.onNodeWithTag("correctAnswerScreen").assertIsDisplayed()
    }

    @Test
    fun correctAnswerPopupIsDisplayed() {
        composeRule.onNodeWithTag("correctAnswerPopup").assertIsDisplayed()
    }

    @Test
    fun correctAnswerScreenContainsCorrectText() {
        val sb = StringBuilder()
        sb.append(guesser).append(" made a correct guess: \nThe word was \"")
            .append(guess)
            .append("\"!")
        composeRule.onNodeWithTag("correctAnswerText").assertTextContains(sb.toString())
    }
}