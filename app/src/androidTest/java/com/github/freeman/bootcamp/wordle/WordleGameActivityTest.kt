package com.github.freeman.bootcamp.wordle

import android.content.Context
import android.content.Intent
import android.text.style.BackgroundColorSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat.startActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordleGameActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<WordleGameActivity>()

    @Test
    fun buttonIsDisplayed() {
        composeRule.onNode(hasText("Submit word")).assertIsDisplayed()
    }

    @Test
    fun textFieldIsDisplayed() {
        composeRule.onNode(hasText("Enter a 5 letters word to submit")).assertIsDisplayed()
    }

    @Test
    fun gridIsDisplayed() {
        composeRule.onNode(hasTestTag("wordle_tile_grid")).assertIsDisplayed()
        for (i in 0 until 40) {
            composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString())).assertIsDisplayed()
        }
    }

    /*
    @Test
    fun gridColorisBlackAtStartOfTheGame() {
        composeRule.onNode(hasTestTag("wordle_tile_grid")).assertIsDisplayed()
        for (i in 0 until 40) {
            composeRule.onNode(hasTestTag("wordle_tile_id_" + i.toString()))
                .assertBackgroundColor(Color(0xff000000))
        }
    }


    fun SemanticsNodeInteraction.assertBackgroundColor(expectedBackground: Color) {
        val capturedName = captureToImage().colorSpace.name
        assertEquals(expectedBackground.colorSpace.name, capturedName)
    }



     */
    /*
    @Test
    fun breaksAgain() {
        val mockIntentFactory = mockk<IntentFactory>(relaxed = true)
        val exampleActivity = spyk(WordleGameActivity(mockIntentFactory))
        every { exampleActivity.startActivity(any()) } returns Unit
        exampleActivity.openOtherActivityWithExtras()
        composeRule.onNode(hasText("Submit word")).assertIsDisplayed()
    }

     */
}


