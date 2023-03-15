package com.github.freeman.bootcamp

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.rememberDrawController

class DrawingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DrawingScreen{}
                }
            }
        }
    }
}

@Composable
fun DrawingScreen(save: (Bitmap) -> Unit) {
    Box {
        Column {
            val controller = rememberDrawController()
            DrawBox(
                drawController = controller,
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
fun DefaultPreview() {
    BootcampComposeTheme {
        DrawingScreen{}
    }
}