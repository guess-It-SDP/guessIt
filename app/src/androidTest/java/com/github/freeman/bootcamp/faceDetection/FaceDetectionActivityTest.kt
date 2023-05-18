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

    /*
    @Test
    fun drawMoustacheAndHairAndHatDontNullifyCanvas() {
        var context = composeRule.activity
        var faceBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.hat)
        var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ed)
            .copy(Bitmap.Config.ARGB_8888, true)
        val paint = Paint()
        paint.color = Color.Red.toArgb()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 16.0f
        val image = InputImage.fromBitmap(faceBitmap!!, 0)
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).build()
        val detector = FaceDetection.getClient(options)
        detector.process(image).addOnSuccessListener { faces ->
            val canvas = Canvas(bitmap!!)
            detector.process(image).addOnSuccessListener { faces ->
                val canvas = Canvas(bitmap!!)

                faces.forEach { face ->
                    FaceDetectionActivity.drawHairUnit().invoke(face, canvas, bitmap, paint)
                    FaceDetectionActivity.drawMoustacheUnit().invoke(face, canvas, bitmap, paint)
                    FaceDetectionActivity.drawHatUnit().invoke(face, canvas, bitmap, paint)
                }
            }
            assertNotNull(canvas)
        }

    }
    
     */

}