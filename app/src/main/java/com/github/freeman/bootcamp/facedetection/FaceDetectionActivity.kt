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
        // The factors by which the distance of the hat from the boundingBox should be adjusted.
        private const val HAT_FOREHEAD_FACTOR = -(1 / 1.5)
        private const val HAT_LEFT_HEAD_FACTOR = -(1 / 3.0)
        private const val HAT_RIGHT_HEAD_FACTOR = (1 / 3.0)
        private const val HAT_BUTTOM_FACTOR = -(1 / 1.5)
        private const val HAIR_TOP_FACTOR = -(1 / 3.0)
        private const val HAIR_BUTTOM_FACTOR = -(1 / 3.0)
        private const val HAIR_LEFT_HEAD_FACTOR = -(1 / 5.0)
        private const val HAIR_RIGHT_HEAD_FACTOR = (1 / 5.0)
        private const val MOUSTACHE_TOP_FACTOR = 0.28
        private const val MOUSTACHE_BUTTOM_FACTOR = 0.28
        private const val MOUSTACHE_LEFT_HEAD_FACTOR = 0.0
        private const val MOUSTACHE_RIGHT_HEAD_FACTOR = -0.0
        const val FACE_DETECTION_TAG = "faceDetectionTag"
        const val FACE_DETECTION_TAG2 = "faceDetectionTag2"

        fun transformBitmapToDrawOnFaces(
            bitmap: Bitmap?, context : Context
        ) :Bitmap{
            var overlayPic = BitmapFactory.decodeResource(context.resources, R.drawable.hat)
            var overlayPic2 = BitmapFactory.decodeResource(context.resources, R.drawable.moustache)
                val image = InputImage.fromBitmap(bitmap!!, 0)
                val options = options()
                val detector = FaceDetection.getClient(options)
                detector.process(image).addOnSuccessListener { faces ->
                    val canvas = Canvas(bitmap)
                    val paint = paint()
                    faces.forEach { face ->

                        if(overlayPic2 != null){     drawHat(face, canvas, overlayPic, paint)
                            drawMoustache(face, canvas, overlayPic2, paint) }
                        else { drawHat(face, canvas, overlayPic, paint)}
                    }
                }
            return  bitmap
            }




        /**
         * Draws objects on the detected faces in the provided bitmap image using the specified overlay pictures and draw function.
         *
         * @param bitmap The bitmap image on which to draw the objects.
         * @param overlayPic The overlay picture to use for drawing on the faces.
         * @param drawFunction The function used to draw the overlay on each face detected.
         * @param overlayPic2 Optional second overlay picture to use for drawing a moustache on the faces. (default: null)
         */
        @Composable
        fun drawObjectsOnFacesOnBitmapImage(
            bitmap: Bitmap?, overlayPic: Bitmap, drawFunction: (Face, Canvas, Bitmap, Paint) -> Unit
        ,overlayPic2: Bitmap? = null
        )  {
            var faceDetected by remember { mutableStateOf(false) }
            LaunchedEffect(true) {
                val image = InputImage.fromBitmap(bitmap!!, 0)
                val options = options()
                val detector = FaceDetection.getClient(options)
                detector.process(image).addOnSuccessListener { faces ->
                    val canvas = Canvas(bitmap!!)
                    val paint = paint()
                    faces.forEach { face ->

                      if(overlayPic2 != null){     drawHat(face, canvas, overlayPic, paint)
                        drawMoustache(face, canvas, overlayPic2, paint) }
                        else {drawFunction(face, canvas, overlayPic, paint) }
                    }

                    faceDetected = true

                }.addOnFailureListener { e ->
                    // Handle the failure here

                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(FACE_DETECTION_TAG)
            ) {
                if (faceDetected) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Face Detection Result",
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(FACE_DETECTION_TAG2)
                    )
                }
            }
        }


        /**
         * Creates and returns a `Paint` object with the desired configuration for drawing.
         *
         * @return The `Paint` object with the specified configuration.
         */
        private fun paint(): Paint {
            val paint = Paint()
            paint.color = Color.Red.toArgb()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 16.0f
            return paint
        }


        /**
         * Creates and returns the face detector options with the desired configuration.
         *
         * @return The face detector options with the specified configuration.
         */
        private fun options(): FaceDetectorOptions {
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).build()
            return options
        }




        /**
         * Returns a function that draws a hat on the provided canvas using the specified overlay picture and paint.
         * The returned function has the signature (Face, Canvas, Bitmap, Paint) -> Unit.
         *
         * @return The function that draws a hat on the canvas.
         */
        fun drawHatUnit(): (Face, Canvas, Bitmap, Paint) -> Unit =
            { face, canvas, overlayPic, paint -> drawHat(face, canvas, overlayPic, paint) }

        /**
         * Returns a function that draws a moustache on the provided canvas using the specified overlay picture and paint.
         * The returned function has the signature (Face, Canvas, Bitmap, Paint) -> Unit.
         *
         * @return The function that draws a moustache on the canvas.
         */
        fun drawMoustacheUnit(): (Face, Canvas, Bitmap, Paint) -> Unit =
            { face, canvas, overlayPic, paint -> drawMoustache(face, canvas, overlayPic, paint) }

        /**
         * Returns a function that draws hair on the provided canvas using the specified overlay picture and paint.
         * The returned function has the signature (Face, Canvas, Bitmap, Paint) -> Unit.
         *
         * @return The function that draws a hat on the canvas.
         */
        fun drawHairUnit(): (Face, Canvas, Bitmap, Paint) -> Unit =
            { face, canvas, overlayPic, paint -> drawHair(face, canvas, overlayPic, paint) }

        /**
         * Draws a hat on the given face in the canvas using the provided face bitmap and paint object.
         *
         * @param face       The face object representing the detected face.
         * @param canvas     The canvas object where the hat will be drawn.
         * @param overlayPic The bitmap object representing the object to add on the face
         * @param paint      The paint object used to draw the hat.
         */
        private fun drawHat(face: Face, canvas: Canvas, overlayPic: Bitmap, paint: Paint) {
            drawStuff(
                face,
                canvas,
                overlayPic,
                paint,
                HAT_FOREHEAD_FACTOR,
                HAT_BUTTOM_FACTOR,
                HAT_LEFT_HEAD_FACTOR,
                HAT_RIGHT_HEAD_FACTOR
            )
        }

        /**
         * Draws a hat on the given face in the canvas using the provided face bitmap and paint object.
         *
         * @param face       The face object representing the detected face.
         * @param canvas     The canvas object where the hat will be drawn.
         * @param overlayPic The bitmap object representing the object to add on the face
         * @param paint      The paint object used to draw the hat.
         */
        private fun drawMoustache(face: Face, canvas: Canvas, overlayPic: Bitmap, paint: Paint) {
            drawStuff(
                face,
                canvas,
                overlayPic,
                paint,
                MOUSTACHE_TOP_FACTOR,
                MOUSTACHE_BUTTOM_FACTOR,
                MOUSTACHE_LEFT_HEAD_FACTOR,
                MOUSTACHE_RIGHT_HEAD_FACTOR
            )
        }

        /**
         * Draws a hair on the given face in the canvas using the provided face bitmap and paint object.
         *
         * @param face       The face object representing the detected face.
         * @param canvas     The canvas object where the hat will be drawn.
         * @param overlayPic The bitmap object representing the object to add on the face
         * @param paint      The paint object used to draw the hat.
         */
        fun drawHair(face: Face, canvas: Canvas, overlayPic: Bitmap, paint: Paint) {
            drawStuff(
                face,
                canvas,
                overlayPic,
                paint,
                HAIR_TOP_FACTOR,
                HAIR_BUTTOM_FACTOR,
                HAIR_LEFT_HEAD_FACTOR,
                HAIR_RIGHT_HEAD_FACTOR
            )
        }


        /**
         * Draws an overlay on the provided `Canvas` based on the specified factors and face parameters.
         *
         * @param face The face object representing the detected face.
         * @param canvas The canvas on which to draw the overlay.
         * @param overlayPic The bitmap representing the overlay to be drawn.
         * @param paint The paint object used for drawing on the canvas.
         * @param topFactor The factor determining the adjustment of the top coordinate of the bounding box.
         * @param botFactor The factor determining the adjustment of the bottom coordinate of the bounding box.
         * @param leftFactor The factor determining the adjustment of the left coordinate of the bounding box.
         * @param rightFactor The factor determining the adjustment of the right coordinate of the bounding box.
         */
        private fun drawStuff(
            face: Face,
            canvas: Canvas,
            overlayPic: Bitmap,
            paint: Paint,
            topFactor: Double,
            botFactor: Double,
            leftFactor: Double,
            rightFactor: Double
        ) {
            val boundingBox = face.boundingBox
            val rect = Rect(
                boundingBox.left + adjustBound(
                    boundingBox.width(), leftFactor
                ), face.boundingBox.top + adjustBound(
                    face.boundingBox.height(), topFactor
                ), boundingBox.right + adjustBound(
                    boundingBox.width(), rightFactor
                ), face.boundingBox.bottom + adjustBound(
                    face.boundingBox.height(), botFactor
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
            return Math.floor(boundsDistance * factor).toInt();
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var bitmap by remember { mutableStateOf<Bitmap?>(null) }
            var context = LocalContext.current
            var hatBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.hat)
            var moustacheBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.moustache)
            bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ed)
                .copy(Bitmap.Config.ARGB_8888, true)
            drawObjectsOnFacesOnBitmapImage(bitmap, hatBitmap, drawHatUnit(),moustacheBitmap)
        }
    }
}
