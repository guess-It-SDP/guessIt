package com.github.freeman.bootcamp.faceDetection

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity2
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class FaceDetectionActivity2Test {

    @get:Rule
    val composeRule = createAndroidComposeRule<FaceDetectionActivity2>()

    @Test
    fun BoxContainingPictureExixst() {
        composeRule.onNodeWithTag(FaceDetectionActivity.FACE_DETECTION_TAG).assertExists()
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