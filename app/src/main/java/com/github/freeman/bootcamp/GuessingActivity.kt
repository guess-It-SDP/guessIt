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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class GuessingActivity : ComponentActivity() {
    private val gameGuessesId = "GameTestGuessesId" //TODO: set when a game is starting
    private val dbrefGuesses = Firebase.database.getReference("Guesses/$gameGuessesId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                GuessingScreen(dbrefGuesses)
            }
        }
    }
}

@Composable
fun GuessItem(guess: Guess) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .testTag("guessItem")
    ) {
        //if guess.guesser.equals(MYNAME) {
        //    Text(text = "$You try \"${guess.guess}\"")
        //} else {
        Text(text = "${guess.guesser} tries \"${guess.guess}\"")
    }
}

@Composable
fun GuessesList(guesses: Array<Guess>) {
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(guesses) { guess ->
            GuessItem(guess = guess)
        }
    }
}

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
fun GuessingScreen(dbrefGuesses: DatabaseReference, gameId: String = LocalContext.current.getString(R.string.default_game_id)) {
    var guesses by remember { mutableStateOf(arrayOf<Guess>()) }
    var guess by remember { mutableStateOf("") }
    val dbrefImages = Firebase.database.getReference("Images/$gameId")

    dbrefGuesses.addValueEventListener(object : ValueEventListener {
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
                            .height(300.dp)
                            .fillMaxWidth()
                            .background(Color.DarkGray)
            ) {
                var bitmap by remember { mutableStateOf<Bitmap>(Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)) }
                dbrefImages.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val decoded = BitmapHandler.stringToBitmap(snapshot.getValue<String>()!!)
                            if (decoded != null) bitmap = decoded
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // do nothing
                    }
                })
                Image(
                    bitmap = bitmap.asImageBitmap(),
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
                GuessesList(guesses = guesses)
            }

            GuessingBar(
                guess = guess,
                onGuessChange = { guess = it },
                onSendClick = {
                    val gs = Guess(guesser = "MyUsername", guess = guess) //TODO: Change the guesser name with my name in the database
                    val guessId = guesses.size.toString() //TODO: Change for a more accurate id
                    dbrefGuesses.child(guessId).setValue(gs)

                    guess = ""
                }
            )
        }
    }
}

@Preview
@Composable
fun GuessingPreview() {
    val guessGameId = "GameTestGuessesId"
    val db = Firebase.database
    db.useEmulator("10.0.2.2", 9000)
    val dbref = Firebase.database.getReference("Guesses/$guessGameId")
    BootcampComposeTheme {
        GuessingScreen(dbref)
    }
}



