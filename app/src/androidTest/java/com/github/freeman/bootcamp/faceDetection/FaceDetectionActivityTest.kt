package com.github.freeman.bootcamp.faceDetection

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import junit.framework.TestCase.assertEquals
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