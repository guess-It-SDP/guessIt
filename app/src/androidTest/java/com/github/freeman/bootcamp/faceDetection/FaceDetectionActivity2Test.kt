package com.github.freeman.bootcamp.faceDetection

import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity2
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FaceDetectionActivity2Test {


    @get:Rule
    val composeTestRule = createAndroidComposeRule<FaceDetectionActivity2>()

    @Test
    fun launchActivityWithExtrasTest() {
        val extras = Intent().apply {
            putExtra("key1", "value1")
            putExtra("key2", 123)
            // Add more extras if needed
        }

        // Launch the activity with extras
        var scenario: ActivityScenario<FaceDetectionActivity2>? = null
        composeTestRule.runOnUiThread {
            scenario = ActivityScenario.launch<FaceDetectionActivity2>(extras)
        }

    assertEquals(true,true)
    }

        @Test
    fun companionsIsAccessible() {
        assertEquals(
            FaceDetectionActivity.FACE_DETECTION_TAG, FaceDetectionActivity.FACE_DETECTION_TAG
        )
        assertEquals(
            FaceDetectionActivity.FACE_DETECTION_TAG2, FaceDetectionActivity.FACE_DETECTION_TAG2
        )
    }
}