package com.github.freeman.bootcamp.faceDetection

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class FaceDetectionActivityTest {

    @get:Rule
    val composeRule =  createAndroidComposeRule<FaceDetectionActivity>()

    @Test
    fun BoxContainingPictureExixst(){
        composeRule.onNodeWithTag(FaceDetectionActivity.FACE_DETECTION_TAG).assertExists()
    }

    @Test
    fun companionsIsAccessible(){
       assertEquals(FaceDetectionActivity.FACE_DETECTION_TAG,FaceDetectionActivity.FACE_DETECTION_TAG)
        assertEquals(FaceDetectionActivity.HAT_FOREHEAD_FACTOR,FaceDetectionActivity.HAT_FOREHEAD_FACTOR)
        assertEquals(FaceDetectionActivity.HAT_RIGHT_HEAD_FACTOR ,FaceDetectionActivity.HAT_RIGHT_HEAD_FACTOR )
        assertEquals(FaceDetectionActivity.HAT_LEFT_HEAD_FACTOR,FaceDetectionActivity.HAT_LEFT_HEAD_FACTOR)
    }

}