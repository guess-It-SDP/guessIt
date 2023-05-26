package com.github.freeman.bootcamp.games.guessit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay


class TimerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        val dbrefTimer = Firebase.database.reference.child(getString(R.string.games_path)).child(gameId).child(getString(R.string.current_timer_path))

        setContent {
            BootcampComposeTheme {
                Surface {
                    TimerScreen(dbrefTimer, 100L)
                }
            }
        }
    }
}

@Composable
fun TimerScreen(dbrefTimer: DatabaseReference, time: Long, size: Int = 70, fontSize: TextUnit = 30.sp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .testTag("timerScreen")
    ) {
        Timer(
            dbrefTimer = dbrefTimer,
            totalTime = time * 1000L,
            firstColor = MaterialTheme.colorScheme.primary,
            secondColor = MaterialTheme.colorScheme.primaryContainer,
            fontSize = fontSize,
            modifier = Modifier
                .size((0.8*size).dp)
                .testTag("timer")
        )
    }
}

@Composable
fun Timer(
    dbrefTimer: DatabaseReference,
    totalTime: Long,
    firstColor: Color,
    secondColor: Color,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val initialValue = 1f
    val strokeWidth = 5.dp

    var size by remember { mutableStateOf(IntSize.Zero) }
    var value by remember { mutableStateOf(initialValue) }
    var currentTime by remember { mutableStateOf(totalTime) }

    LaunchedEffect(key1 = currentTime) {
        if (currentTime > 0) {
            delay(100L)
            currentTime -= 100L
            value = currentTime / totalTime.toFloat()
        } else {
            dbrefTimer.setValue(context.getString(R.string.timer_over))
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged { size = it }
    ) {
        TimerCircles(modifier, firstColor, secondColor, strokeWidth, value)

        TimerText(currentTime, fontSize, secondColor)
    }
}

@Composable
fun TimerText(currentTime: Long, fontSize: TextUnit, textColor: Color) {
    Text(
        text = (currentTime / 1000L).toString(),
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        color = textColor
    )
}

@Composable
fun TimerCircles(modifier: Modifier, activeBarColor: Color, inactiveBarColor: Color, strokeWidth: Dp, value: Float) {
    Canvas(
        modifier = modifier.testTag("timerCanvas")) {

        drawArc(
            color = inactiveBarColor,
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
