package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingScreen
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawingActivityTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    private fun setContent() {
        composeRule.setContent {
            val dbref = getGameDBRef(context)
            DrawingScreen(dbref,"127")
        }
    }

//    @Test
//    fun drawingScreenIsDisplayed() {
//        setContent()
//        composeRule.onNodeWithTag(context.getString(R.string.drawing_screen)).assertIsDisplayed()
//    }
//
//    @Test
//    fun colorButtonHasClickAction() {
//        setContent()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_color))
//            .assertHasClickAction()
//    }
//
//    @Test
//    fun widthButtonHasClickAction() {
//        setContent()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
//            .assertHasClickAction()
//    }
//
//    @Test
//    fun undoButtonHasClickAction() {
//        setContent()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.undo))
//            .assertHasClickAction()
//    }
//
//    @Test
//    fun redoButtonHasClickAction() {
//        setContent()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.redo))
//            .assertHasClickAction()
//    }
//
//    @Test
//    fun doneButtonHasClickAction() {
//        setContent()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.drawing_done))
//            .assertHasClickAction()
//    }
//
//    @Test
//    fun widthSliderAppearsOnWidthButtonClick() {
//        setContent()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
//            .performClick()
//        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
//    }
//
//    @Test
//    fun sliderStaysOnScreenWhenClickingUndo() {
//        setContent()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
//            .performClick()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.undo)).performClick()
//        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
//    }
//
//    @Test
//    fun sliderStaysOnScreenWhenClickingRedo() {
//        setContent()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
//            .performClick()
//        composeRule.onNodeWithContentDescription(context.getString(R.string.redo)).performClick()
//        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
//    }
//
//    @Test
//    fun drawingScreenContainsTimer() {
//        setContent()
//        composeRule.onNode(hasTestTag("timerScreen")).assertIsDisplayed()
//    }
}