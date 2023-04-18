package com.github.freeman.bootcamp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.CorrectAnswerPopUp
import com.github.freeman.bootcamp.games.guessit.TimerOverPopUp
import com.github.freeman.bootcamp.games.guessit.TimerScreen
import com.github.freeman.bootcamp.games.guessit.guessing.Guess
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PopUpsTest {
    @get:Rule
    val composeRule = createComposeRule()

     private val guess = Guess(guesser = "Me", guess = "ThePerfectWord")

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
        composeRule.onNodeWithTag("popUpBox").assertIsDisplayed()
    }

    @Test
    fun correctAnswerPopUpContainsCorrectText() {
        setPopUp("correctAnswer")
        val text = guess.guesser + " made a correct guess: \n\nThe word was \"" + guess.guess
        composeRule.onNodeWithTag("popUpText").assertTextContains(text)
    }

    @Test
    fun timerOverPopUpIsDisplayed() {
        setPopUp("timerOver")
        composeRule.onNodeWithTag("popUpScreen").assertIsDisplayed()
    }

    @Test
    fun timerOverPopUpContainsCorrectText() {
        setPopUp("correctAnswer")
        val text = "The time is over!"
        composeRule.onNodeWithTag("popUpText").assertTextContains(text)
    }

}