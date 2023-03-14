package com.github.freeman.bootcamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class GuessingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                Guessing()
            }
        }
    }
}

@Composable
fun GuessItem(guess: Guess) {
    Row(modifier = Modifier.padding(8.dp)) {
        Text(text = "${guess.guesser}: ${guess.guess}")
    }
}

@Composable
fun GuessesList(guesses: List<Guess>) {
    LazyColumn (modifier = Modifier.fillMaxWidth()) {
        items(guesses) { guess ->
            GuessItem(guess = guess)
        }
    }
}

@Preview
@Composable
fun GuessesListPreview() {
    val guesses = remember { mutableStateListOf<Guess>() }
    guesses.add(Guess(guesser = "Alban", guess = "Coucou"))
    guesses.add(Guess(guesser = "Clara", guess = "Salut"))
    guesses.add(Guess(guesser = "Alban", guess = "blablabla"))
    guesses.add(Guess(guesser = "Clara", guess = "bloubloublou"))

    GuessesList(guesses)
}

@Composable
fun GuessingScreen(
    guesses: List<Guess>,
    guess: String,
    onGuessChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column {
        Text(
            text = "Make a guess !",
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color.Gray)
        ) {
            GuessesList(guesses = guesses)

        }
        //Spacer(modifier = Modifier.weight(1f))
        GuessingBar(
            guess = guess,
            onGuessChange = onGuessChange,
            onSendClick = onSendClick
        )
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
        modifier = Modifier.fillMaxWidth()
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
                onClick = onSendClick,
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = "OK")
            }
        }
    }
}

@Composable
fun Guessing() {
    val guesses = remember { mutableStateListOf<Guess>() }
    val guess = remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GuessingScreen(
                guesses = guesses,
                guess = guess.value,
                onGuessChange = { guess.value = it },
                onSendClick = {
                    guesses.add(Guess(guesser = guess.value, guess = "me"))
                    guess.value = ""
                }
            )
        }
    }
}

@Preview
@Composable
fun GuessingPreview() {
    Guessing()
}



