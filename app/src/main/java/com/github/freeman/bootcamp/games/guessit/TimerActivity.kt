package com.github.freeman.bootcamp.games.guessit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.ui.theme.Purple80
import kotlinx.coroutines.delay


class TimerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                TimerScreen(100, 100L)
            }
        }
    }
}

@Composable
fun TimerScreen(size: Int, time: Long, color: Color= Purple80) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .testTag("timerScreen")
    ) {
        Timer(
            totalTime = time * 1000L,
            activeBarColor = color,
            modifier = Modifier
                .size((0.8*size).dp)
                .testTag("timer")
        )
    }
}

@Composable
fun Timer(
    totalTime: Long,
    activeBarColor: Color,
    modifier: Modifier = Modifier
) {
    val initialValue = 1f
    val strokeWidth = 5.dp

    var size by remember { mutableStateOf(IntSize.Zero) }
    var value by remember { mutableStateOf(initialValue) }
    var currentTime by remember { mutableStateOf(totalTime) }
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if (currentTime > 0 && isTimerRunning) {
            delay(100L)
            currentTime -= 100L
            value = currentTime / totalTime.toFloat()
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged { size = it }
    ) {
        TimerCircles(modifier, activeBarColor, strokeWidth, value)

        TimerText(currentTime)
    }
}

@Composable
fun TimerText(currentTime: Long) {
    Text(
        text = (currentTime / 1000L).toString(),
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray
    )
}

@Composable
fun TimerCircles(modifier: Modifier, activeBarColor: Color, strokeWidth: Dp, value: Float) {
    Canvas(
        modifier = modifier.testTag("timerCanvas")) {

        drawArc(
            color = Color.DarkGray,
            startAngle = 90f,
            sweepAngle = 360f,
            useCenter = false,
            size = Size(size.width, size.height),
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        drawArc(
            color = activeBarColor,
            startAngle = 90f,
            sweepAngle = 360f * value,
            useCenter = false,
            size = Size(size.width, size.height),
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    TimerScreen(100, 100L)
}
