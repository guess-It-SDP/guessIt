package com.github.freeman.bootcamp

import android.R
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.rememberDrawController

class DrawingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                DrawingScreen{}
                TimerScreen(100, 100L)
            }
        }
    }
}

@Composable
fun DrawingScreen(save: (Bitmap) -> Unit) {
    Box (Modifier.testTag("drawingScreen")){
        Column {
            val controller = rememberDrawController()
            DrawBox(
                drawController = controller,
                modifier = Modifier
                    .fillMaxSize(),
                bitmapCallback = { imageBitmap, error ->
                    imageBitmap?.let {
                        save(it.asAndroidBitmap())
                    }
                }
            )
            controller.changeColor(Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrawingScreenPreview() {
    DrawingScreen{}
}