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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                Surface {
                    Column {
                        TopAppbarCredits()
                        CreditsDisplay()
                    }
                }
            }

        }
    }

    companion object {
        const val TOPBAR_CREDITS_TEXT = "Guess It! Credits"

        val CREDITS = listOf(
            "ni16s This game was developed in 2023 as part of the “Software Development Project” course at EPFL.\n",
            "tn30s Developers",
            "nn16n Michael Freeman\nDanny Seel\nClara Tavernier\nDavid Lacour\nPaul Guillon",
            "tn30s Professor",
            "nn16n George Canda",
            "tn30s Supervisors",
            "nn16n Can Cebedi\nMathieu Marchand")
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
                val isTitle = (text.first() == 't')
                val fontStyle = text[1]
                val fontSize = text.substring(2, 4).toInt()
                val space = text[4]

                val color: Color =
                    if (isTitle) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.tertiary

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
                        .testTag(text.drop(6).take(5)),
                    text = text.drop(6),
                    fontSize = fontSize.sp,
                    fontStyle = style,
                    fontWeight = weight,
                    textAlign = TextAlign.Center,
                    color = color
                )
            }
        }
    }
}

@Composable
fun TopAppbarCredits(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarCredits"),
        title = {
            Text(
                text = TOPBAR_CREDITS_TEXT,
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