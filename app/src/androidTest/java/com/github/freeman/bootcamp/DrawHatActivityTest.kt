package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.github.freeman.bootcamp.DrawHatActivity.Companion.HAT_HELP
import com.github.freeman.bootcamp.DrawHatActivity.Companion.YOUR_HAT
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawHatActivityTest {
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

    @Before
    fun init() {
        FirebaseEmulator.init()
        val storageRef = FirebaseSingletons.storage.get().storage.reference

        composeRule.setContent {
            DrawHatScreen(storageRef)
        }
    }

    @Test
    fun backButtonIsClickable() {
        composeRule.onNode(hasTestTag("drawHatBackButton")).assertHasClickAction()
    }

    @Test
    fun drawHatScreenIsDisplayed() {
        composeRule.onNodeWithTag(context.getString(R.string.draw_hat_screen)).assertIsDisplayed()
    }

    // From DrawingActivityTest
    @Test
    fun colorButtonIsClickable() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_color))
            .assertHasClickAction()
    }

    // From DrawingActivityTest
    @Test
    fun widthButtonIsClickable() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width)).assertHasClickAction()
    }

    // From DrawingActivityTest
    @Test
    fun undoButtonIsClickable() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.undo)).assertHasClickAction()
    }

    // From DrawingActivityTest
    @Test
    fun redoButtonIsClickable() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.redo)).assertHasClickAction()
    }

    // From DrawingActivityTest
    @Test
    fun doneButtonIsClickable() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.drawing_done)).assertHasClickAction()
    }

    @Test
    fun yourHatTextIsDisplayed() {
        composeRule.onNodeWithTag(YOUR_HAT).assertIsDisplayed()
    }

    @Test
    fun helpInformationTextIsDisplayed() {
        composeRule.onNodeWithTag(HAT_HELP).assertIsDisplayed()
    }

    @Test
    fun usersHatImageIsDisplayed() {
        composeRule.onNodeWithTag("usersHat", useUnmergedTree = true).assertIsDisplayed()
    }

    // From DrawingActivityTest
    @Test
    fun widthSliderAppearsOnWidthButtonClick() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width)).performClick()
        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
    }

    // From DrawingActivityTest
    @Test
    fun sliderStaysOnScreenWhenClickingUndo() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width)).performClick()
        composeRule.onNodeWithContentDescription(context.getString(R.string.undo)).performClick()
        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
    }

    // From DrawingActivityTest
    @Test
    fun sliderStaysOnScreenWhenClickingRedo() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
            .performClick()
        composeRule.onNodeWithContentDescription(context.getString(R.string.redo)).performClick()
        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
    }
}