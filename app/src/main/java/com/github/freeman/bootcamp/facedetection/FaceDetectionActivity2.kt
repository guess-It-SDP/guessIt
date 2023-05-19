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
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import com.google.mlkit.vision.face.Face

/**
 * USED FOR TESTING ONLY
 */
class FaceDetectionActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var bitmap by remember { mutableStateOf<Bitmap?>(null) }
            var context = LocalContext.current
            var hatBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.hat)
            var moustacheBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.moustache)
            bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ed)
                .copy(Bitmap.Config.ARGB_8888, true)
            FaceDetectionActivity.drawObjectsOnFacesOnBitmapImage(bitmap, hatBitmap,  FaceDetectionActivity.drawHairUnit(),moustacheBitmap)
        }
    }
}
