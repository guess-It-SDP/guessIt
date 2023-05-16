package com.github.freeman.bootcamp.games.help

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                HelpScreen()
            }
        }
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
        // 'Guess It!' credits
        ElevatedButton(
            modifier = Modifier.testTag("creditsButton"),
            onClick = {
                context.startActivity(Intent(context, GuessItRulesActivity::class.java))
            }
        ) {
            Text("Game Credits")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rules of "Guess It!" online game
        ElevatedButton(
            modifier = Modifier.testTag("guessItRulesButton"),
            onClick = {
                context.startActivity(Intent(context, GuessItRulesActivity::class.java))
            }
        ) {
            Text("Guess It! rules")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rules of "Wordle" offline game
        ElevatedButton(
            modifier = Modifier.testTag("guessItRulesButton"),
            onClick = {
                context.startActivity(Intent(context, WordleRulesActivity::class.java))
            }
        ) {
            Text("Wordle rules")
        }
    }

}