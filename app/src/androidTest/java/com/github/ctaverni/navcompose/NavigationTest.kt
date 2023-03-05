package com.github.ctaverni.navcompose

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ctaverni.navcompose.ui.theme.BootcampComposeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    val composeRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity


    @Test
    fun mainScreenIsDisplayed() {
        setMainScreen()
        composeRule.onNode(hasTestTag("mainScreen")).assertIsDisplayed()
    }

    @Test
    fun mainScreenTextIsCorrect() {
        setMainScreen()
        composeRule.onNode(hasTestTag("homeScreen")).assertTextContains("Home Screen")
    }

    @Test
    fun buttonHasClickAction() {
        setMainScreen()
        composeRule.onNode(hasTestTag("mainScreen"))
    }


    @Test
    fun mainScreenPreviewTest() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainScreenPreview()
            }
        }

        composeRule.onRoot().apply {
            assertIsDisplayed()
            composeRule.onNode(hasTestTag("navigationDrawer")).assertIsDisplayed()
        }
    }





    private fun setMainScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainScreen()
            }
        }
    }
}