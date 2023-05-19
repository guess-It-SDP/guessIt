package com.github.freeman.bootcamp.faceDetection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Rule
import org.junit.Test

class FaceDetectionActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<FaceDetectionActivity>()

    @Test
    fun BoxContainingPictureExixst() {
        composeRule.onNodeWithTag(FaceDetectionActivity.FACE_DETECTION_TAG).assertExists()
    }

    @Test
    fun companionsIsAccessible() {
        assertEquals(
            FaceDetectionActivity.FACE_DETECTION_TAG, FaceDetectionActivity.FACE_DETECTION_TAG
        )
    }
}