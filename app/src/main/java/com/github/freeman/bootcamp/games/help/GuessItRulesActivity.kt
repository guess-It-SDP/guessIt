package com.github.freeman.bootcamp.games.help

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.games.help.GuessItRulesActivity.Companion.GUESSIT_RULES
import com.github.freeman.bootcamp.games.help.GuessItRulesActivity.Companion.GUESSIT_RULES_TITLE
import com.github.freeman.bootcamp.games.help.GuessItRulesActivity.Companion.TOPBAR_GUESSIT_RULES_TEXT
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class GuessItRulesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                Column {
                    TopAppbarGuessItRules()
                    GuessItRulesDisplay()
                }
            }

        }
    }

    companion object {
        const val TOPBAR_GUESSIT_RULES_TEXT = "GUESS IT! RULES"
        const val GUESSIT_RULES_TITLE = "How to play?"

        val GUESSIT_RULES = listOf(
            
            ("iThe rules of Wordle are elegantly simple.\n"),
            "iYour objective is to guess a secret five-letter word in as few guesses as possible.\n",
            "nTo submit a guess, type any five-letter word and press enter.\n")
    }
}

@Composable
fun GuessItRulesDisplay() {
    Column(
        modifier = Modifier
            .testTag("wordleRulesScreen")
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            modifier = Modifier.testTag("how_to_play"),
            text = GUESSIT_RULES_TITLE,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )

        LazyColumn(
            modifier = Modifier
                .testTag("guessItRulesText")
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            items(GUESSIT_RULES) { text ->
                val style: FontStyle =
                    if (text.first() == 'n') FontStyle.Normal
                    else FontStyle.Italic

                Text(
                    text = text.drop(1),
                    fontSize = 16.sp,
                    fontStyle = style
                )
            }
        }
    }
}

@Composable
fun TopAppbarGuessItRules(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarGuessItRules"),
        title = {
            Text(
                modifier = Modifier.testTag("topBarGuessItRulesTitle"),
                text = TOPBAR_GUESSIT_RULES_TEXT,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                val activity = (context as? Activity)
                activity?.finish()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}