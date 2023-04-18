package com.github.freeman.bootcamp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.TimerScreen
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun timerScreenIsDisplayed() {
        setTimerScreen()
        composeRule.onNodeWithTag("timerScreen").assertIsDisplayed()
    }

    private fun setTimerScreen() {
        val dbref = Firebase.database.getReference("games/testgameid/current/current_timer")
        composeRule.setContent {
            BootcampComposeTheme {
                TimerScreen(dbref, 100, 100L)
            }
        }
    }
}