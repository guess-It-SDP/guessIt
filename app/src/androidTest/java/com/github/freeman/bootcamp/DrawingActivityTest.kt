package com.github.freeman.bootcamp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawingActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<DrawingActivity>()

    @Test
    fun drawingScreenIsDisplayed() {
        composeRule.onNode(hasTestTag("drawingScreen")).assertIsDisplayed()
    }

    @Test
    fun drawingScreenContainsTimer() {
        composeRule.onNode(hasTestTag("timerScreen")).assertIsDisplayed()
    }
}