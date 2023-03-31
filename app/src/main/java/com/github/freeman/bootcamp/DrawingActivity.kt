package com.github.freeman.bootcamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawController
import io.ak1.drawbox.rememberDrawController
import io.ak1.rangvikalp.RangVikalp

// This class uses two great libraries :
// - For the drawing zone : DrawBox (https://github.com/akshay2211/DrawBox)
// - For the color picker : Rang-Vikalp (https://github.com/akshay2211/rang-vikalp)

private val DBREF = Firebase.database.getReference("Images")
private val DEFAULT_COLOR = black
private const val DEFAULT_WIDTH = 15f

class DrawingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawingScreen()
        }
    }
}

// The drawing screen is made of a drawing zone and a controls bar
@Composable
fun DrawingScreen(
    dbref: DatabaseReference = DBREF,
    gameId: String = LocalContext.current.getString(R.string.default_game_id)
) {
    val undoVisibility = remember { mutableStateOf(false) }
    val redoVisibility = remember { mutableStateOf(false) }
    val colorBarVisibility = remember { mutableStateOf(false) }
    val widthSliderVisibility = remember { mutableStateOf(false) }
    val currentWidth = remember { mutableStateOf(DEFAULT_WIDTH) }
    val currentColor = remember { mutableStateOf(DEFAULT_COLOR) }
    val drawController = rememberDrawController()
    val firstStroke = remember { mutableStateOf(true) }
    if (firstStroke.value) {
        drawController.changeColor(DEFAULT_COLOR)
        drawController.changeStrokeWidth(DEFAULT_WIDTH)
        firstStroke.value = false
    }
    Box(Modifier.testTag(LocalContext.current.getString(R.string.drawing_screen))) {
        Column {
            // Controls bar
            ControlsBar(
                drawController,
                onColorClick =
                {
                    colorBarVisibility.value = !colorBarVisibility.value
                    widthSliderVisibility.value = false
                },
                onWidthClick =
                {
                    widthSliderVisibility.value = !widthSliderVisibility.value
                    colorBarVisibility.value = false
                },
                undoVisibility,
                redoVisibility,
                currentColor,
            )
            // Color picker that appears when clicking on one of the two color selection buttons
            RangVikalp(
                isVisible = colorBarVisibility.value,
                colorIntensity = 0,
                showShades = false,
                colors = colorArray,
                defaultColor = DEFAULT_COLOR
            )
            {
                currentColor.value = it
                drawController.changeColor(it)
            }
            // Slider to select stroke width that appears when clicking the corresponding button in
            // the controls bar
            if (widthSliderVisibility.value) {
                Slider(
                    value = currentWidth.value,
                    onValueChange = { newValue ->
                        currentWidth.value = newValue
                        drawController.changeStrokeWidth(newValue)
                    },
                    valueRange = 5f..50f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colors.primaryVariant,
                        activeTrackColor = MaterialTheme.colors.primary,
                        inactiveTrackColor = MaterialTheme.colors.secondary
                    ),
                    modifier = Modifier.testTag(LocalContext.current.getString(R.string.width_slider))
                )
            }
            // Drawing zone
            DrawBox(
                drawController = drawController,
                backgroundColor = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = false),
                bitmapCallback = { imageBitmap, _ -> // Tells the drawController what to do when drawController.saveBitmap() is called
                    imageBitmap?.let {
                        dbref.child(gameId)
                            .setValue(BitmapHandler.bitmapToString(it.asAndroidBitmap()))
                    }
                }
            ) { undoCount, redoCount ->
                colorBarVisibility.value = false
                undoVisibility.value = undoCount != 0
                redoVisibility.value = redoCount != 0
            }
        }
    }
}

// The controls bar offers buttons that allow to undo, redo, select color and stoke width.
@Composable
private fun ControlsBar(
    drawController: DrawController,
    onColorClick: () -> Unit,
    onWidthClick: () -> Unit,
    undoVisibility: MutableState<Boolean>,
    redoVisibility: MutableState<Boolean>,
    colorValue: MutableState<Color>,
) {
    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
        TimerScreen(100, 60L)
        MenuItems(
            R.drawable.ic_undo,
            LocalContext.current.getString(R.string.undo),
            if (undoVisibility.value) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
        ) {
            if (undoVisibility.value) drawController.unDo()
        }
        MenuItems(
            R.drawable.ic_redo,
            LocalContext.current.getString(R.string.redo),
            if (redoVisibility.value) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
        ) {
            if (redoVisibility.value) drawController.reDo()
        }
        MenuItems(
            R.drawable.ic_color,
            LocalContext.current.getString(R.string.stroke_color),
            colorValue.value
        ) {
            onColorClick()
        }
        MenuItems(
            R.drawable.ic_width,
            LocalContext.current.getString(R.string.stroke_width),
            MaterialTheme.colors.primary
        ) {
            onWidthClick()
        }
        MenuItems(
            R.drawable.ic_sharp_arrow_circle_up,
            LocalContext.current.getString(R.string.drawing_done),
            MaterialTheme.colors.primary
        ) {
            drawController.saveBitmap()
        }
    }
}

// Represents a button in the controls bar
@Composable
private fun RowScope.MenuItems(
    @DrawableRes resId: Int,
    desc: String,
    colorTint: Color,
    border: Boolean = false,
    onClick: () -> Unit
) {
    val modifier = Modifier.size(24.dp)
    IconButton(
        onClick = onClick, modifier = Modifier.weight(1f, true)
    ) {
        Icon(
            painterResource(id = resId),
            contentDescription = desc,
            tint = colorTint,
            modifier = if (border) modifier.border(
                0.5.dp,
                Color.Black,
                shape = CircleShape
            ) else modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DrawingScreenPreview() {
    DrawingScreen()
}