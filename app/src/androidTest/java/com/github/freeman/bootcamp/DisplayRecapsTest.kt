package com.github.freeman.bootcamp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayRecapsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setTheContentBefore(){
        composeRule.setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }

        composeRule.onNodeWithTag("displayRecapsButton").performClick()
    }

    @Test
    fun topAppBarIsDisplayed() {
        composeRule.onNodeWithTag("topAppbarDisplayRecaps").assertIsDisplayed()
    }
}