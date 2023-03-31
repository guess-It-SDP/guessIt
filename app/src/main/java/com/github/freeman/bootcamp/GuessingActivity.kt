@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.freeman.bootcamp

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.github.freeman.bootcamp.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.ui.theme.Purple40
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

/**
 * The activity where the guesser tries to guess what is the drawing
 */
class GuessingActivity : ComponentActivity() {
    private lateinit var dbrefGames: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getStringExtra("gameId").toString()
        dbrefGames = Firebase.database.getReference("Games/$gameId")

        setContent {
            BootcampComposeTheme {
                GuessingScreen(dbrefGames)
            }
        }
    }
}

/**
 * Displays of one guess (with the guesser name)
 */
@Composable
fun GuessItem(guess: Guess, answer: String) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .testTag("guessItem")
    ) {
        if (guess.guess?.lowercase()  == answer.lowercase()) {
            Popup {
                val gs = Guess(guess.guesser, answer)
                CorrectAnswerScreen(gs = gs)
            }
        }

        Text(text = "${guess.guesser} tries \"${guess.guess}\"")
    }
}

/**
 * Displays all guesses that have been made in the game
 */
@Composable
fun GuessesList(guesses: Array<Guess>, answer: String) {
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(guesses) { guess ->
            GuessItem(guess, answer)
        }
    }
}

/**
 * The writing bar where guessers can enten their guesses
 */
@Composable
fun GuessingBar(
    guess: String,
    onGuessChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guessingBar")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                value = guess,
                onValueChange = onGuessChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a guess...") },
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
                Text(text = "OK")
            }
        }
    }
}

@Composable
fun GuessingScreen(dbrefGames: DatabaseReference, gameId: String = LocalContext.current.getString(R.string.default_game_id)) {
    var guesses by remember { mutableStateOf(arrayOf<Guess>()) }
    var guess by remember { mutableStateOf("") }
    val dbrefImages = Firebase.database.getReference("Images/$gameId")

    dbrefGames.child("Guesses").addValueEventListener(object : ValueEventListener {
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

    //The drawing sent by the drawer to the guessers
    var bitmap by remember { mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap()) }
    dbrefImages.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val decoded = BitmapHandler.stringToBitmap(snapshot.getValue<String>()!!)
                if (decoded != null) bitmap = decoded.asImageBitmap()
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // do nothing
        }
    })

    //the correct answer of the round
    var answer = ""
    dbrefGames.child("topic").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                answer = dataSnapshot.getValue<String>()!!
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // do nothing
        }
    })

    //the username of the current user
    var username = ""
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val dbrefUsername = Firebase.database.reference.child("Profiles/$uid").child("username")
    FirebaseUtilities.databaseGet(dbrefUsername)
        .thenAccept {
            username = it
        }

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .testTag("guessingScreen")
        ) {
            Text(
                    text = "Your turn to guess!",
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
                        .background(Color.DarkGray)
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "drawn image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
                    .align(Alignment.End)
                    .testTag("guessesList")
            ) {
                GuessesList(guesses = guesses, answer = answer)
            }

            GuessingBar(
                guess = guess,
                onGuessChange = { guess = it },
                onSendClick = {
                    val gs = Guess(guesser = username, guess = guess)
                    val guessId = guesses.size.toString() //TODO: Change for a more accurate id ?
                    dbrefGames.child("Guesses").child(guessId).setValue(gs)

                    guess = ""
                }
            )
        }
    }
}

/**
 * The popup screen when a user guesses the correct answer
 */
@Composable
fun CorrectAnswerScreen(gs: Guess) {
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    val guesser = Firebase.database.getReference("Profiles/${gs.guesser}").child("uid").get().toString()

    val sb = StringBuilder()
    if (currentUser.equals(guesser)) {
        sb.append("You")
    } else {
        sb.append(gs.guesser)
    }

    sb.append(" made a correct guess: \nThe word was \"")
        .append(gs.guess)
        .append("\"!")

    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .testTag("correctAnswerScreen")
            .background(Color.Transparent)
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .testTag("correctAnswerPopup")
                .size(275.dp, 130.dp)
                .clip(shape)
                .background(Purple40),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.testTag("correctAnswerText"),
                text = sb.toString(),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun GuessingPreview() {
    val guessGameId = "GameTestGuessesId"
    val answer = "Flower"

    val db = Firebase.database
    db.useEmulator("10.0.2.2", 9000)
    val dbref = Firebase.database.getReference("Games/$guessGameId")
    BootcampComposeTheme {
        GuessingScreen(dbref)
    }
}



