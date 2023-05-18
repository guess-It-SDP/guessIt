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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.games.help.CreditsActivity.Companion.CREDITS
import com.github.freeman.bootcamp.games.help.CreditsActivity.Companion.TOPBAR_CREDITS_TEXT
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class CreditsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                Column {
                    TopAppbarCredits()
                    CreditsDisplay()
                }
            }

        }
    }

    companion object {
        const val TOPBAR_CREDITS_TEXT = "GUESS IT! CREDITS"

        val CREDITS = listOf(
            "i16s This game was developed in 2023 as part of the “Software Development Project” course at EPFL.\n",
            "n30s Developers",
            "n16n Michael Freeman",
            "n16n Danny Seel",
            "n16n Clara Tavernier",
            "n16n David Lacour",
            "n16n Paul Guillon",
            "n30s Professor",
            "n16n George Canda",
            "n30s Supervisors",
            "n16n Can Cebedi",
            "n16n Mathieu Marchand")
    }
}

@Composable
fun CreditsDisplay() {
    Column(
        modifier = Modifier
            .testTag("creditsScreen")
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyColumn(
            modifier = Modifier
                .testTag("creditsText")
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(CREDITS) { text ->
                val fontStyle = text.first()
                val fontSize = text.substring(1, 3).toInt()
                val space = text[3]

                val style: FontStyle =
                    if (fontStyle == 'n') FontStyle.Normal
                    else FontStyle.Italic

                val weight: FontWeight =
                    if (fontSize == 16) FontWeight.Normal
                    else FontWeight.Bold

                if (space == 's') {
                    Spacer(modifier = Modifier.size(20.dp))
                }

                Text(
                    modifier = Modifier
                        .testTag(text.drop(5).take(5))
                        .align(Alignment.CenterHorizontally),
                    text = text.drop(5),
                    fontSize = fontSize.sp,
                    fontStyle = style,
                    fontWeight = weight,
                )
            }
        }
    }
}

@Composable
fun TopAppbarCredits(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarCreditsRules"),
        title = {
            Text(
                modifier = Modifier.testTag("topBarCreditsTitle"),
                text = TOPBAR_CREDITS_TEXT,
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