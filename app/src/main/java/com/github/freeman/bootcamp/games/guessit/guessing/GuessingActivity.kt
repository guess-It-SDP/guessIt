@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.freeman.bootcamp.games.guessit.guessing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import com.github.freeman.bootcamp.games.guessit.ScoreScreen
import com.github.freeman.bootcamp.games.guessit.TimerOverPopUp
import com.github.freeman.bootcamp.games.guessit.TimerScreen
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.GUESSING_BOTTOMBAR_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.GUESSING_BOTTOMBAR_TEXT
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.NO_ARTIST
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.SCREEN_TEXT
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.WAITING_TEXT
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.answer
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.bitmap
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.pointsReceived
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.roundNb
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.turnNb
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.BitmapHandler
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.github.freeman.bootcamp.utilities.rememberImeState
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

/**
 * The activity where the guesser tries to guess what is the drawing
 */
class GuessingActivity : ComponentActivity() {
    private lateinit var dbrefGame: DatabaseReference
    override fun onBackPressed() {} // prevent going back by sliding left or pressing back button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        dbrefGame = getGameDBRef(this, gameId)
        val storageGameRef = Firebase.storage.reference.child(getString(R.string.game_recaps_path)).child(gameId)

        setContent {
            BootcampComposeTheme {
                GuessingScreen(dbrefGame, this, storageGameRef, LocalLifecycleOwner.current)
            }
        }
    }

    override fun onStop() {
        pointsReceived = false
        super.onStop()
    }

    companion object {
        const val GUESSING_BOTTOMBAR_TEXT = "Type a guess..."
        const val GUESSING_BOTTOMBAR_BUTTON_TEXT = "OK"
        const val NO_ARTIST = "No artist"
        const val WAITING_TEXT = "Please wait while the artist selects a word to draw."
        const val SCREEN_TEXT = "Your turn to guess!"

        var pointsReceived = false
        lateinit var answer: String
        var roundNb = 0
        var turnNb = 0
        var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap()
    }
}

/**
 * Displays of one guess (with the guesser name)
 */
@Composable
@SuppressLint("RestrictedApi")
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalZeroShutterLag::class)
fun GuessItem(guess: Guess, storageGameRef: StorageReference) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(8.dp)
            .testTag("guessItem")
    ) {
        val userId = Firebase.auth.currentUser?.uid
        if (guess.message?.lowercase() != answer.lowercase()) {
            Text(text = "${guess.guesser} : ${guess.message}")
        } else if (guess.guesserId == userId) {
            Text(text = "${guess.guesser} : ${guess.message} (Correct!)")
        } else {
            Text(text = "${guess.guesser} : ****")
        }
    }
}

private fun storeDrawing(
    storageGameRef: StorageReference,
    userId: String?,
    context: Context
) {
    val storageDrawingsRef = storageGameRef.child(userId.toString())
        .child(context.getString(R.string.drawings_folder_name))
        .child(context.getString(R.string.drawing_file_name))
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val tempFile = File.createTempFile(context.getString(R.string.drawing_file_name), null)
    tempFile.writeBytes(byteArrayOutputStream.toByteArray())
    storageDrawingsRef.putFile(tempFile.toUri())
}

@SuppressLint("RestrictedApi")
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalZeroShutterLag::class)
private fun takeSelfie(
    storageGameRef: StorageReference,
    userId: String?,
    context: Context,
    lifecycleOwner : LifecycleOwner
) {
    val storageSelfieRef = storageGameRef.child(userId.toString())
        .child(context.getString(R.string.selfies_folder_name))
        .child(context.getString(R.string.selfie_file_name))
    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(CAPTURE_MODE_ZERO_SHUTTER_LAG)
        .setCameraSelector(CameraSelector.DEFAULT_FRONT_CAMERA)
        .build()
    val tempFile = File.createTempFile(context.getString(R.string.selfie_file_name), null)
    val outputFileOptions = OutputFileOptions.Builder(tempFile).build()
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context)
    val processCameraProvider = cameraProviderFuture.get()
    if (processCameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
        processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_FRONT_CAMERA,
            imageCapture
        )
        val cameraExecutor = Executors.newSingleThreadExecutor()
        val onImageSavedCallback = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val selfieBitmap = BitmapFactory.decodeStream(tempFile.inputStream())
                FaceDetectionActivity.transformBitmapToDrawOnFaces(selfieBitmap, context)
                val bos = ByteArrayOutputStream()
                selfieBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                val bitmapData = bos.toByteArray()
                val fos = FileOutputStream(tempFile)
                fos.write(bitmapData)
                fos.flush()
                fos.close()
                storageSelfieRef.putFile(Uri.fromFile(tempFile))
                Log.i("Selfie", "Image saved")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.d("Selfie", exception.message.toString())
            }
        }
        imageCapture.takePicture(outputFileOptions, cameraExecutor, onImageSavedCallback)
    }
}

/**
 * Displays all guesses that have been made in the game
 */
@Composable
fun GuessesList(guesses: Array<Guess>, dbrefGame: DatabaseReference, artistId: String, storageGameRef: StorageReference) {
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(guesses) { guess ->
            GuessItem(guess, storageGameRef)
        }
    }
}

/**
 * The writing bar where guessers can enter their guesses
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuessingBar(
    guess: String,
    onGuessChange: (String) -> Unit,
    onSendClick: () -> Unit,
    scrollState: ScrollState
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guessingBar")
            .verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                value = guess,
                onValueChange = onGuessChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(GUESSING_BOTTOMBAR_TEXT) },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier.testTag("guessButton"),
                onClick = onSendClick,
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = GUESSING_BOTTOMBAR_BUTTON_TEXT)
            }
        }
    }
}

@Composable
fun GuessingScreen(dbrefGame: DatabaseReference, context: Context, storageGameRef: StorageReference,
                   lifecycleOwner: LifecycleOwner) {
    val scrollState = rememberScrollState()
    var guesses by remember { mutableStateOf(arrayOf<Guess>()) }
    var guess by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf("") }

    //the timer of the game
    timer = ""
    val dbrefTimer = dbrefGame.child(context.getString(R.string.current_timer_path))
    dbrefTimer.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                timer = snapshot.getValue<String>()!!
            }
        }
        override fun onCancelled(error: DatabaseError) {
            // do nothing
        }
    })

    //the guesses made by the guessers
    dbrefGame.child(context.getString(R.string.guesses_path))
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val guessesList = snapshot.getValue<ArrayList<Guess>>()!!
                    guesses = guessesList.toTypedArray()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }
        })

    //the current round and turn (in the round)
    FirebaseUtilities.databaseGet(dbrefGame.child(context.getString(R.string.current_round_path)))
        .thenAccept {
            roundNb = it.toInt()
        }
    FirebaseUtilities.databaseGet(dbrefGame.child(context.getString(R.string.current_turn_path)))
        .thenAccept {
            turnNb = it.toInt()
        }

    //the correct answer of the round
    answer = ""
    val dbrefAnswer = dbrefGame
        .child(context.getString(R.string.topics_path))
        .child(roundNb.toString())
        .child(turnNb.toString())
        .child(context.getString(R.string.topic_path))
    dbrefAnswer.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                answer = snapshot.getValue<String>()!!
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // do nothing
        }

    })

    //the username of the current user
    var username = ""
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val dbrefUsername = Firebase.database.reference
        .child(context.getString(R.string.profiles_path))
        .child("$uid")
        .child(context.getString(R.string.username_path))
    FirebaseUtilities.databaseGet(dbrefUsername)
        .thenAccept {
            username = it
        }

    // Fetch the ID of the current artist
    val currentArtist = remember {
        mutableStateOf(NO_ARTIST)
    }
    FirebaseUtilities.databaseGet(dbrefGame.child(context.getString(R.string.current_artist_path)))
        .thenAccept {
            currentArtist.value = it
        }

    //The drawing sent by the drawer to the guessers
    val dbrefImages = dbrefGame
        .child(context.getString(R.string.topics_path))
        .child(roundNb.toString())
        .child(turnNb.toString())
        .child(context.getString(R.string.drawing_path))
    var displayedBitmap by remember { mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap()) }
    dbrefImages.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val decoded = BitmapHandler.stringToBitmap(snapshot.getValue<String>()!!)
                if (decoded != null) {
                    displayedBitmap = decoded.asImageBitmap()
                    bitmap = decoded.asImageBitmap()
                }
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // do nothing
        }
    })

    // Stores and updates a boolean used to know if the artist is picking a word to draw
    // Finishes the activity if necessary
    var topicSelection by remember { mutableStateOf(false) }
    val dbrefCurrentState = dbrefGame.child(context.getString(R.string.current_state_path))
    dbrefCurrentState.addValueEventListener(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val currentState = snapshot.getValue<String>()!!
                if (currentState != context.getString(R.string.state_newturn)
                    && currentState != context.getString(R.string.state_topicselection)
                    && currentState != context.getString(R.string.state_playturn)) {
                    val activity = context as? Activity
                    activity?.finish()
                } else {
                    topicSelection = currentState == context.getString(R.string.state_topicselection)
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            // do nothing
        }
    })


    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .testTag("guessingScreen")
        ) {
            Text(
                text = SCREEN_TEXT,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .testTag("guessText"),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                if (topicSelection) {
                    Text(
                        text = WAITING_TEXT,
                        modifier = Modifier
                            .align(Alignment.Center),
                        color = Color.DarkGray
                    )
                } else {
                    Image(
                        bitmap = displayedBitmap,
                        contentDescription = "drawn image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }

                if (timer == context.getString(R.string.timer_inprogress)) {
                    TimerScreen(dbrefTimer, 60L, fontSize = 30.sp)
                }

                ScoreScreen(dbrefGame)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
                    .align(Alignment.End)
                    .testTag("guessesList")
            ) {
                GuessesList(guesses = guesses, dbrefGame = dbrefGame,
                    artistId = currentArtist.value, storageGameRef = storageGameRef)
            }

            if (timer == context.getString(R.string.timer_over)) {
                TimerOverPopUp()
            } else {
                GuessingBar(
                    guess = guess,
                    onGuessChange = { guess = it },
                    onSendClick = {
                        val gs = Guess(guesser = username, guesserId = uid, message = guess)
                        val guessId = guesses.size.toString()
                        dbrefGame
                            .child(context.getString(R.string.guesses_path))
                            .child(guessId)
                            .setValue(gs)

                        val userId = Firebase.auth.currentUser?.uid
                        if (gs.message?.lowercase() == answer.lowercase()) {
                            val dbGuesserScoreRef = dbrefGame
                                .child(context.getString(R.string.players_path))
                                .child(userId.toString())
                                .child(context.getString(R.string.score_path))
                            val dbArtistScoreRef = dbrefGame
                                .child(context.getString(R.string.players_path))
                                .child(currentArtist.value)
                                .child(context.getString(R.string.score_path))
                            val correctGuessesRef = dbrefGame.child(context.getString(R.string.current_correct_guesses_path))

                            // Take Selfie of the guesser for the game recap and save it to Firebase storage
                            takeSelfie(storageGameRef, userId, context, lifecycleOwner)
                            // Store drawing for the game recap
                            storeDrawing(storageGameRef, userId, context)

                            // Increase the points of the artist if they haven't already received points this round
                            FirebaseUtilities.databaseGetLong(correctGuessesRef)
                                .thenAccept { nbGuesses ->
                                    // Give the points to the player who guessed correctly
                                    FirebaseUtilities.databaseGetLong(dbGuesserScoreRef)
                                        .thenAccept { score ->
                                            // If the artist hasn't yet received points for this drawing, grant them
                                            if (nbGuesses.toInt() == 0) {
                                                FirebaseUtilities.databaseGetLong(dbArtistScoreRef)
                                                    .thenAccept { artistsPoints ->
                                                        dbArtistScoreRef.setValue(artistsPoints + 1)
                                                    }
                                            }

                                            // Increase current player's points
                                            if (!pointsReceived) {
                                                Log.d("GuessingD","pointsReceived: $pointsReceived")
                                                dbGuesserScoreRef.setValue(score + 1)
                                                correctGuessesRef.setValue(nbGuesses + 1)
                                                pointsReceived = true
                                            }
                                        }
                                }

                            // Take Selfie of the guesser for the game recap and save it to Firebase storage
//                            val lifecycleOwner = LocalLifecycleOwner.current
                            //takeSelfie(storageGameRef, userId, context, lifecycleOwner)
                            // Store drawing for the game recap
//                            storeDrawing(storageGameRef, userId, context)

                            val gs = Guess(gs.guesser, gs.guesserId, answer)
                        }




                        guess = ""
                    },
                    scrollState,
                )
            }

        }
    }
}