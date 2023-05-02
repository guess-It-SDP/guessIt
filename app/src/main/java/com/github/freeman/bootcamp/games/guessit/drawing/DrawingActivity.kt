package com.github.freeman.bootcamp.games.guessit.drawing

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings.Global.getString
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.black
import com.github.freeman.bootcamp.colorArray
import com.github.freeman.bootcamp.games.guessit.TimerOverPopUp
import com.github.freeman.bootcamp.games.guessit.TimerScreen
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingActivity.Companion.roundNb
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingActivity.Companion.turnNb
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.BitmapHandler
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.videocall.APP_ID
import com.github.freeman.bootcamp.videocall.VideoScreen
import com.github.freeman.bootcamp.videocall.VideoScreen2
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraVideoViewer
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawController
import io.ak1.drawbox.rememberDrawController
import io.ak1.rangvikalp.RangVikalp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// This class uses two great libraries :
// - For the drawing zone : DrawBox (https://github.com/akshay2211/DrawBox)
// - For the color picker : Rang-Vikalp (https://github.com/akshay2211/rang-vikalp)

private val DEFAULT_COLOR = black
private const val DEFAULT_WIDTH = 15f

class DrawingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        val dbref = getGameDBRef(this, gameId)

        setContent {
            DrawingScreen(dbref,gameId)
        }
    }

    companion object {
        var roundNb = 0
        var turnNb = 0
    }
}

// The drawing screen is made of a drawing zone and a controls bar
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DrawingScreen(
    dbref: DatabaseReference,
    gameId: String
) {
    val context = LocalContext.current

    // timer of the artist
    var timer by remember { mutableStateOf("") }
    val dbrefTimer = dbref.child(context.getString(R.string.current_timer_path))
    dbrefTimer.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                timer = snapshot.getValue<String>()!!
            }
        }
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })

    //the current round and turn (in the round) and topic corresponding
    FirebaseUtilities.databaseGet(dbref.child(context.getString(R.string.current_round_path)))
        .thenAccept {
            roundNb = it.toInt()
        }
    FirebaseUtilities.databaseGet(dbref.child(context.getString(R.string.current_turn_path)))
        .thenAccept {
            turnNb = it.toInt()
        }

    val undoVisibility = remember { mutableStateOf(false) }
    val redoVisibility = remember { mutableStateOf(false) }
    val colorBarVisibility = remember { mutableStateOf(false) }
    val widthSliderVisibility = remember { mutableStateOf(false) }
    val currentWidth = remember { mutableStateOf(DEFAULT_WIDTH) }
    val currentColor = remember { mutableStateOf(DEFAULT_COLOR) }
    val drawController = rememberDrawController()
    val composableScope = rememberCoroutineScope()
    val firstStroke = remember { mutableStateOf(true) }
    if (firstStroke.value) {
        drawController.changeColor(DEFAULT_COLOR)
        drawController.changeStrokeWidth(DEFAULT_WIDTH)
        firstStroke.value = false
    }

    Box(Modifier.testTag(context.getString(R.string.drawing_screen))) {
        Column {
            // Controls bar
            ControlsBar(
                dbref,
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
                    modifier = Modifier.testTag(context.getString(R.string.width_slider))
                )
            }

            if (timer == context.getString(R.string.timer_over)) {
                TimerOverPopUp()
            } else {
                // Drawing zone
                DrawBox(
                    drawController = drawController,
                    backgroundColor = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f, fill = false),
                    bitmapCallback = { imageBitmap, _ -> // Tells the drawController what to do when drawController.saveBitmap() is called
                        imageBitmap?.let {
                            dbref.child(context.getString(R.string.topics_path))
                                .child(roundNb.toString())
                                .child(turnNb.toString())
                                .child(context.getString(R.string.drawing_path))
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

    // Thread that sends the drawing to firebase every 50ms (20fps)
    composableScope.launch {
        while (true) {
            delay(50L)
            drawController.saveBitmap()
        }
    }
}

// The controls bar offers buttons that allow to undo, redo, select color and stoke width.
@Composable
private fun ControlsBar(
    dbref: DatabaseReference,
    drawController: DrawController,
    onColorClick: () -> Unit,
    onWidthClick: () -> Unit,
    undoVisibility: MutableState<Boolean>,
    redoVisibility: MutableState<Boolean>,
    colorValue: MutableState<Color>,
) {
    val context = LocalContext.current

    val dbrefTimer = dbref.child(context.getString(R.string.current_timer_path))

    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
        TimerScreen(dbrefTimer,60L)
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
            context.getString(R.string.stroke_color),
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
    val context = LocalContext.current
    val dbref = getGameDBRef(context)
    DrawingScreen(dbref,"127")
}