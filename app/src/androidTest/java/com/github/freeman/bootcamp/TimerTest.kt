package com.github.freeman.bootcamp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.TimerScreen
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setTimerScreen() {
        composeRule.setContent {
            FirebaseEmulator.init()

            val context = LocalContext.current

            val dbref = Firebase.database.reference
                .child(context.getString(R.string.games_path))
                .child(context.getString(R.string.test_game_id))
                .child(context.getString(R.string.current_timer_path))

            BootcampComposeTheme {
                TimerScreen(dbref, 100L)
            }
        }
    }

    @Test
    fun timerScreenIsDisplayed() {
        composeRule.onNodeWithTag("timerScreen").assertIsDisplayed()
    }
}