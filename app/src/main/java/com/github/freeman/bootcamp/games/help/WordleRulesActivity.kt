package com.github.freeman.bootcamp.games.help

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.games.help.WordleRulesActivity.Companion.RULES
import com.github.freeman.bootcamp.games.help.WordleRulesActivity.Companion.TOPBAR_WORDLE_RULES_TEXT
import com.github.freeman.bootcamp.games.help.WordleRulesActivity.Companion.WORDLE_RULES_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class WordleRulesActivity : ComponentActivity() {

    companion object {
        const val TOPBAR_WORDLE_RULES_TEXT = "WORDLE RULES"
        const val WORDLE_RULES_TITLE = "How to play?"

        val RULES = listOf("The rules of Wordle are elegantly simple.\n",
            "Your objective is to guess a secret five-letter word in as few guesses as possible.\n",
            "To submit a guess, type any five-letter word and press enter.\n",
            "If words only mode is activated, all of your guesses must be real words, according to " +
                    "a dictionary of five-letter words that Wordle allows as guesses. You can’t " +
                    "make up a non-existent word, like AEIOU, just to guess those letters.\n",
            "As soon as you’ve submitted your guess, the game will color-code each letter in your " +
                    "guess to tell you how close it was to the letters in the hidden word.\n",
            "A Red square means that this letter does not appear in the secret word at all.\n",
            "A yellow square means that this letter appears in the secret word, but it’s in the " +
                    "wrong spot within the word.\n",
            "A green square means that this letter appears in the secret word, and it’s in " +
                    "exactly the right place.\n",
            "Getting a green square or yellow square will get you closer to guessing the real " +
                    "secret word, since it means you’ve guessed a correct letter.\n",
            "For example, let’s say you guess “WRITE” and get two green squares on the W and " +
                    "the R, and gray squares for the I, T, and E. Your next guess might be WRONG," +
                    " WRACK, or WRUNG, since these words start with WR and don’t contain the " +
                    "letters I, T, or E.\n",
            "Alternatively, let’s say you guess “WRITE” and get two green squares on the T and " +
                    "the E, and gray squares for the W, R, and I. In that case, your next guess " +
                    "might be BASTE, ELATE, or LATTE, since these words end with TE and don’t " +
                    "contain the letters W, R, or I.\n",
            "Remember that the same letter can appear multiple times in the secret word, and " +
                    "there’s no special color coding for letters that appear repeatedly. For " +
                    "example, if the secret word is BELLE and you guess a word with one L and " +
                    "one E, Wordle won’t tell you that both those letters actually appear " +
                    "twice.\n")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                Surface {
                    Column {
                        TopAppbarWordleRules()
                        WordleRulesDisplay()
                    }
                }
            }

        }
    }
}

@Composable
fun WordleRulesDisplay() {
    Column(
        modifier = Modifier
            .testTag("wordleRulesScreen")
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            modifier = Modifier.testTag("how_to_play"),
            text = WORDLE_RULES_TITLE,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )

        LazyColumn(
            modifier = Modifier
                .testTag("wordleRulesText")
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            items(RULES) { text ->
                Text(
                    text = text,
                    fontSize = 16.sp
                )
            }
        }

    }

}

@Composable
fun TopAppbarWordleRules(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarWordleRules"),
        title = {
            Text(
                modifier = Modifier.testTag("topBarWordleRulesTitle"),
                text = TOPBAR_WORDLE_RULES_TEXT,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 20.sp
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                val activity = (context as? Activity)
                activity?.finish()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}