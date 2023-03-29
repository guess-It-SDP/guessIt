package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
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
        composeRule.onNodeWithTag("drawingScreen").assertIsDisplayed()
    }

    @Test
    fun colorButtonHasClickAction() {
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.stroke_color)).assertHasClickAction()
    }

    @Test
    fun widthButtonHasClickAction() {
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.stroke_width)).assertHasClickAction()
    }

    @Test
    fun undoButtonHasClickAction() {
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.undo)).assertHasClickAction()
    }

    @Test
    fun redoButtonHasClickAction() {
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.redo)).assertHasClickAction()
    }

    @Test
    fun widthSliderAppearsOnWidthButtonClick() {
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.stroke_width)).performClick()
        composeRule.onNodeWithTag(composeRule.activity.getString(R.string.width_slider)).assertIsDisplayed()
    }

    @Test
    fun drawingScreenContainsTimer() {
        composeRule.onNode(hasTestTag("timerScreen")).assertIsDisplayed()
    }
}