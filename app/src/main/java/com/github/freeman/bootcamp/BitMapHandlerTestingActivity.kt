package com.github.freeman.bootcamp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import kotlin.random.Random

class BitMapHandlerTestingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BitMapHandlerScreen()
                }
            }
        }
    }
}

@Composable
fun BitMapHandlerScreen() {
    val handler = BitmapHandler("1")
    val toUploadBitmap = createRandomBitmap(100, 100)
    Image(
        toUploadBitmap.asImageBitmap(),
        contentDescription = "bitmap ready to be uploaded to firebase"
    )
    handler.uploadBitmap(toUploadBitmap)
    val fetchedBitmap = handler.fetchBitmap("1")
    if (fetchedBitmap != null) {
        Image(
            fetchedBitmap.asImageBitmap(),
            contentDescription = "fetched bitmap from firebase"
        )
    } else {
        Text("NULL BITMAP")
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BootcampComposeTheme {
        BitMapHandlerScreen()
    }
}

private fun createRandomBitmap(width: Int, height: Int): Bitmap {
    val random = Random(System.currentTimeMillis())
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    for (x in 0 until width) {
        for (y in 0 until height) {
            paint.color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
            canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
        }
    }
    return bitmap
}