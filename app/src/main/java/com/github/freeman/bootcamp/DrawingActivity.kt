package com.github.freeman.bootcamp

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

private val action: MutableState<Any?> = mutableStateOf(null)
private val path = Path()

class DrawingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                CreateCanvas()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateCanvas() {
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    action.value = it
                    path.moveTo(it.x, it.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    action.value = it
                    path.lineTo(it.x, it.y)
                }
                MotionEvent.ACTION_UP -> { }
                else -> false
            }
            true
        }
    ) {
        action.value?.let {
            drawPath(
                path = path,
                color = Color.Green,
                alpha = 1f,
                style = Stroke(10f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BootcampComposeTheme {
        CreateCanvas()
    }
}