package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

/**
 * Shows a screen where you can either join or create a Guess It game
 */
class CreateJoinActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                TopAppbarCreateJoin()

                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("createJoin"),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Text(
                        modifier = Modifier
                            .testTag("createJoinText1"),
                        text = "Let's have fun!",
                        fontSize = 30.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.size(60.dp))
                    Text(
                        modifier = Modifier
                            .testTag("createJoinText2"),
                        fontSize = 20.sp,
                        text = "What do you want to do?",
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    CreateGameButton()
                    Spacer(modifier = Modifier.size(6.dp))
                    JoinGameButton()
                }
            }
        }
    }
}

@Composable
fun TopAppbarCreateJoin(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarCreateJoin"),
        title = {
            Text(
                text = "Guess It!",
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

/**
 * Sends the user in the GameOptionsActivity, from this activity the player can chose which settings
 * he wants to use in his future game.
 */
@Composable
fun CreateGameButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("createGameButton"),
        onClick = { context.startActivity(Intent(context, GameOptionsActivity::class.java)) }
    ) {
        Text("Create a new game")
    }
}

@Composable
fun JoinGameButton() {
    val context = LocalContext.current
    ElevatedButton(
        onClick = { context.startActivity(Intent(context, LobbyListActivity::class.java)) }
    ) {
        Text("Join an existing game")
    }
}