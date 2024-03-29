package com.github.freeman.bootcamp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.DrawHatActivity.Companion.DRAW_HAT
import com.github.freeman.bootcamp.DrawHatActivity.Companion.HAT_HELP
import com.github.freeman.bootcamp.DrawHatActivity.Companion.YOUR_HAT
import com.github.freeman.bootcamp.games.guessit.drawing.*
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawController
import io.ak1.drawbox.rememberDrawController
import io.ak1.rangvikalp.RangVikalp
import java.io.ByteArrayOutputStream
import java.io.File

class DrawHatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storageRef = Firebase.storage.reference
        setContent {
            BootcampComposeTheme {
                DrawHatScreen(storageRef)
            }
        }
    }

    companion object {
        const val DRAW_HAT = "Draw Your Hat"
        const val HAT_HELP = "Welcome! Please draw and upload any hat for your in-game video filter" +
                " by clicking the upload symbol in the right corner."
        const val YOUR_HAT = "Your hat"
    }
}

// Note: DrawingActivity could unfortunately not be reused as it contains references to a specific
// game, contains a timer, uses the realtime database as opposed to the storage one, etc.
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DrawHatScreen(storageRef: StorageReference) {
    val context = LocalContext.current

    val userId = Firebase.auth.currentUser?.uid
    val hatRef = storageRef.child(context.getString(R.string.profiles_path))
        .child(userId.toString()).child(context.getString(R.string.hat))

    // Fetch the user's current hat
    val hat = remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }
    LaunchedEffect(Unit) {
        FirebaseUtilities.storageGet(hatRef)
            .thenAccept {
                hat.value = it
            }
    }

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

    Surface {
        Box(Modifier.testTag(context.getString(R.string.draw_hat_screen))) {
            Column {
                TopAppBarDrawHat()

                Row {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .border(width = 2.dp, color = Color.Black)
                        ) {
                            // The user's last hat
                            Image(
                                painter = rememberAsyncImagePainter(hat.value),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            text = YOUR_HAT,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .testTag(YOUR_HAT)
                        )
                    }

                    Text(
                        text = HAT_HELP,
                        modifier = Modifier
                            .padding(10.dp)
                            .testTag(HAT_HELP),
                        fontSize = 20.sp,
                    )
                }

                // Controls bar with the eraser option
                DrawHatControlsBar(
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
                    onEraseClick = {
                        undoVisibility.value = false
                        redoVisibility.value = false
                    },
                    currentColor,
                    currentWidth
                )

                // Colour picker
                RangVikalp(
                    isVisible = colorBarVisibility.value,
                    colorIntensity = 0,
                    showShades = false,
                    colors = colorArray,
                    defaultColor = DEFAULT_COLOR
                ) {
                    currentColor.value = it
                    drawController.changeColor(it)
                }

                // Slider to select stroke width
                if (widthSliderVisibility.value) {
                    Slider(
                        value = currentWidth.value,
                        onValueChange = { newValue ->
                            currentWidth.value = newValue
                            drawController.changeStrokeWidth(newValue)
                        },
                        valueRange = 5f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.surfaceVariant,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.testTag(context.getString(R.string.width_slider))
                    )
                }

                Row {
                    // Drawing zone
                    DrawBox(
                        drawController = drawController,
                        backgroundColor = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f, fill = false),
                        bitmapCallback = { imageBitmap, _ ->
                            imageBitmap?.let {
                                // Store (and potentially overwrite) the player's hat
                                val byteArrayOutputStream = ByteArrayOutputStream()
                                it.asAndroidBitmap()
                                    .compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                val byteArray = byteArrayOutputStream.toByteArray()
                                val tempFile = File.createTempFile("image", ".png")
                                tempFile.writeBytes(byteArray)
                                hatRef.putFile(tempFile.toUri())
                                hat.value = it.asAndroidBitmap()
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
    }
}


// Note: Please see the comments for DrawHatScreen
@Composable
private fun DrawHatControlsBar(
    drawController: DrawController,
    onColorClick: () -> Unit,
    onWidthClick: () -> Unit,
    undoVisibility: MutableState<Boolean>,
    redoVisibility: MutableState<Boolean>,
    colorValue: MutableState<Color>,
    onEraseClick: () -> Unit,
    currentColor: MutableState<Color>,
    currentWidth: MutableState<Float>,
) {
    val context = LocalContext.current
    val isToggled = remember { mutableStateOf(false) }

    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
        MenuItems(
            R.drawable.ic_undo,
            LocalContext.current.getString(R.string.undo),
            if (undoVisibility.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        ) {
            if (undoVisibility.value) drawController.unDo()
        }
        MenuItems(
            R.drawable.ic_redo,
            LocalContext.current.getString(R.string.redo),
            if (redoVisibility.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
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
            MaterialTheme.colorScheme.primary
        ) {
            onWidthClick()
        }
        // Erases by drawing over the image in white
        ToggleButton(
            R.drawable.black_eraser,
            R.drawable.white_eraser,
            context.getString(R.string.toggleEraser),
            MaterialTheme.colorScheme.primary,
            onClick = {
                // Toggle the eraser on or off
                if (!isToggled.value) {
                    drawController.changeColor(Color.White)
                    drawController.changeStrokeWidth(currentWidth.value)
                    isToggled.value = !isToggled.value
                    onEraseClick()
                } else {
                    drawController.changeColor(currentColor.value)
                    drawController.changeStrokeWidth(currentWidth.value)
                    isToggled.value = !isToggled.value
                }
            },
            isToggled = isToggled
        )

        MenuItems(
            R.drawable.ic_sharp_arrow_circle_up,
            LocalContext.current.getString(R.string.drawing_done),
            MaterialTheme.colorScheme.primary
        ) {
            drawController.saveBitmap()
        }
    }
}

@Composable
fun RowScope.ToggleButton(
    @DrawableRes resOffId: Int,
    @DrawableRes resOnId: Int,
    desc: String,
    colorTint: Color,
    border: Boolean = false,
    onClick: () -> Unit,
    isToggled: MutableState<Boolean>,
) { 
    val modifier = Modifier.size(24.dp).testTag("usersHat")
    IconButton(
        onClick = onClick, modifier = Modifier.weight(1f, true)
    ) {
        Icon(
            painter = if (isToggled.value) {
                painterResource(resOnId)
            } else {
                painterResource(resOffId)
            },
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

@Composable
fun TopAppBarDrawHat(context: Context = LocalContext.current) {
    TopAppBar(
        modifier = Modifier.testTag("topAppbarDrawHat"),
        title = {
            Text(
                modifier = Modifier.testTag("topAppbarDrawHatTitle"),
                text = DRAW_HAT,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(
                onClick = {
                    val activity = (context as? Activity)
                    activity?.finish()
                },
                modifier = Modifier.testTag("drawHatBackButton")
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}