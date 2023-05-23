package com.github.freeman.bootcamp.games.help

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.MainMenuButton
import com.github.freeman.bootcamp.games.help.HelpActivity.Companion.CREDITS_BUTTON
import com.github.freeman.bootcamp.games.help.HelpActivity.Companion.HELP_TITLE
import com.github.freeman.bootcamp.games.help.HelpActivity.Companion.OFFLINE_RULES
import com.github.freeman.bootcamp.games.help.HelpActivity.Companion.ONLINE_RULES
import com.github.freeman.bootcamp.games.help.HelpActivity.Companion.TOPBAR_HELP_TEXT
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                Surface {
                    TopAppbarHelp()
                    HelpScreen()
                }
            }
        }
    }

    companion object {
        const val TOPBAR_HELP_TEXT = "Help"
        const val HELP_TITLE = "How may I help you?"
        const val CREDITS_BUTTON = "Game Credits"
        const val ONLINE_RULES = "Guess It! rules"
        const val OFFLINE_RULES = "Wordle rules"
    }

}

@Composable
fun HelpScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .testTag("helpScreen")
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .testTag("helpText")
                .align(Alignment.CenterHorizontally),
            text = HELP_TITLE,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.size(20.dp))

        // Credits
        MainMenuButton(
            testTag = "creditsButton",
            onClick = { context.startActivity(Intent(context, CreditsActivity::class.java)) },
            text = CREDITS_BUTTON,
            icon = ImageVector.vectorResource(R.drawable.group)
        )

        // Rules of "Guess It!" online game
        MainMenuButton(
            testTag = "guessItRulesButton",
            onClick = { context.startActivity(Intent(context, GuessItRulesActivity::class.java)) },
            text = ONLINE_RULES,
            icon = ImageVector.vectorResource(R.drawable.rules)
        )

        // Rules of "Wordle" offline game
        MainMenuButton(
            testTag = "wordleRulesButton",
            onClick = { context.startActivity(Intent(context, WordleRulesActivity::class.java)) },
            text = OFFLINE_RULES,
            icon = ImageVector.vectorResource(R.drawable.rules)
        )
    }

}

@Composable
fun TopAppbarHelp(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarHelp"),
        title = {
            Text(
                modifier = Modifier.testTag("topBarHelpTitle"),
                text = TOPBAR_HELP_TEXT,
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