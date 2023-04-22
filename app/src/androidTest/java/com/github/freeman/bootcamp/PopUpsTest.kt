package com.github.freeman.bootcamp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.CorrectAnswerPopUp
import com.github.freeman.bootcamp.games.guessit.TimerOverPopUp
import com.github.freeman.bootcamp.games.guessit.guessing.Guess
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PopUpsTest {
    @get:Rule
    val composeRule = createComposeRule()

     private val guess = Guess(guesser = "Me", guesserId = "ID", guess = "ThePerfectWord")

    private fun setPopUp(type: String) {
        composeRule.setContent {
            BootcampComposeTheme {
                if (type == "correctAnswer") {
                    CorrectAnswerPopUp(gs = guess)
                } else if (type == "timerOver") {
                    TimerOverPopUp()
                }
            }
        }
    }

    @Test
    fun correctAnswerPopUpIsDisplayed() {
        setPopUp("correctAnswer")
        composeRule.onNodeWithTag("popUpScreen").assertIsDisplayed()
    }

    @Test
    fun correctAnswerPopupIsDisplayed() {
        setPopUp("correctAnswer")
        composeRule.onNodeWithTag("popUpBox").assertIsDisplayed()
    }

    @Test
    fun correctAnswerBoxContainsCorrectText() {
        setPopUp("correctAnswer")

        val sb = StringBuilder()
        sb.append(guess.guesser).append(" made a correct guess: \n\nThe word was \"")
            .append(guess.guess)
            .append("\"!")

        composeRule.onNodeWithTag("popUpText").assertTextContains(sb.toString())
    }

    @Test
    fun timerOverPopUpIsDisplayed() {
        setPopUp("timerOver")
        composeRule.onNodeWithTag("popUpScreen").assertIsDisplayed()
    }

    @Test
    fun timerOveroxIsDisplayed() {
        setPopUp("timerOver")
        composeRule.onNodeWithTag("popUpBox").assertIsDisplayed()
    }

    @Test
    fun timerOverPopUpContainsCorrectText() {
        setPopUp("timerOver")
        val text = "The time is over!"
        composeRule.onNodeWithTag("popUpText").assertTextContains(text)
    }

}