package com.github.freeman.bootcamp.games.guessit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.github.freeman.bootcamp.games.guessit.guessing.Guess
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.ui.theme.Purple40
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun PopUpScreen(
    text: String
) {
    val shape = RoundedCornerShape(12.dp)

    Popup {
        Column(
            modifier = Modifier
                .testTag("popUpScreen")
                .background(Color.Transparent)
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .testTag("popUpBox")
                    .clip(shape)
                    .background(Purple40),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .testTag("popUpText")
                        .padding(15.dp),
                    text = text,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }

}

/**
 * The popup screen when a user guesses the correct answer
 */
@Composable
fun CorrectAnswerPopUp(gs: Guess) {
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    val guesser = Firebase.database.getReference("profiles/${gs.guesser}").child("uid").get().toString()

    val sb = StringBuilder()
    if (currentUser.equals(guesser)) {
        sb.append("You")
    } else {
        sb.append(gs.guesser)
    }

    sb.append(" made a correct guess: \n\nThe word was \"")
        .append(gs.guess)
        .append("\"!")

    PopUpScreen(text = sb.toString())
}

/**
 * The popup screen when the timer is over
 */
@Composable
fun TimerOverPopUp() {
    val text = "The time is over!"

    PopUpScreen(text = text)
}