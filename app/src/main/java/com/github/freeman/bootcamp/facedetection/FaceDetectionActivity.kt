package com.github.freeman.bootcamp.facedetection

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.mlkit.vision.face.FaceDetectorOptions
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.*
import com.google.mlkit.vision.face.FaceDetection
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.google.mlkit.vision.common.InputImage
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.compose.runtime.*
import com.github.freeman.bootcamp.R
import androidx.compose.ui.graphics.toArgb
import com.google.mlkit.vision.face.Face

/**
 * A class that provides face detection functionality and draws on it
 */
class FaceDetectionActivity : ComponentActivity() {
    companion object {
        const val HAT_FOREHEAD_FACTOR = -1.5 // The factors by which the distance of the hat from the boundingBox should be adjusted.
        const val HAT_LEFT_HEAD_FACTOR = -3.0
        const val HAT_RIGHT_HEAD_FACTOR = 3.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var bitmap by remember { mutableStateOf<Bitmap?>(null) }
            var context = LocalContext.current
            var faceBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.hat)
            bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.my_image2)
                .copy(Bitmap.Config.ARGB_8888, true)
            drawHatOnBitmapImage(bitmap, faceBitmap)
        }
    }

    /**
     * Draws a hat on top of faces detected in a given bitmap image, using the provided overlay picture.
     *
     * @param bitmap The bitmap image on which to draw the hat.
     * @param overlayPic The overlay picture to use for the hat.
     */
    @Composable
    fun drawHatOnBitmapImage(bitmap: Bitmap?, overlayPic : Bitmap ) {
        var faceDetected by remember { mutableStateOf(false) }
        LaunchedEffect(true) {
            val image = InputImage.fromBitmap(bitmap!!, 0)
            val options = options()
            val detector = FaceDetection.getClient(options)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    val canvas = Canvas(bitmap!!)
                    val paint = paint()
                    faces.forEach { face ->
                        drawHat(face, canvas, overlayPic, paint)
                    }

                    faceDetected = true

                }
                .addOnFailureListener { e ->
                    // Handle the failure here

                }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (faceDetected) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Face Detection Result",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
private fun paint(): Paint {
    val paint = Paint()
    paint.color = Color.Red.toArgb()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 16.0f
    return paint
}
private fun options(): FaceDetectorOptions {
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()
    return options
}

/**
 * Draws a hat on the given face in the canvas using the provided face bitmap and paint object.
 *
 * @param face       The face object representing the detected face.
 * @param canvas     The canvas object where the hat will be drawn.
 * @param faceBitmap The bitmap object representing the face image.
 * @param paint      The paint object used to draw the hat.
 */
private fun drawHat(face: Face, canvas: Canvas, overlayPic: Bitmap, paint: Paint) {
    val boundingBox = face.boundingBox
    val rect = Rect(
        boundingBox.left + adjustBound(
            boundingBox.width(),
            FaceDetectionActivity.HAT_LEFT_HEAD_FACTOR
        ), face.boundingBox.top + adjustBound(
            face.boundingBox.height(),
            FaceDetectionActivity.HAT_FOREHEAD_FACTOR
        ), boundingBox.right + adjustBound(
            boundingBox.width(),
            FaceDetectionActivity.HAT_RIGHT_HEAD_FACTOR
        ), face.boundingBox.bottom + adjustBound(
            face.boundingBox.height(),
            FaceDetectionActivity.HAT_FOREHEAD_FACTOR
        )
    )
    canvas.drawBitmap(overlayPic, null, rect, paint)
}

/**
 * Adjusts the distance of the face bounding box based on a given factor.
 *
 * @param boundsDistance The distance of the face bounding box to be adjusted.
 * @param factor         The factor by which the distance of the bounding box should be adjusted.
 * @return The adjusted distance of the face bounding box.
 */
private fun adjustBound(boundsDistance: Int, factor: Double): Int {
    return Math.floor(boundsDistance / factor).toInt();
}

@Composable
fun FaceDetectionScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mainMenuScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    }
}