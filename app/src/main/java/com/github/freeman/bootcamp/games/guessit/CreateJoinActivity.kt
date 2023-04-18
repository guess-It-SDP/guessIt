package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class CreateJoinActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                TopAppbarCreateJoin()

                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("mainMenuScreen"),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
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

@Composable
fun CreateGameButton() {
    val context = LocalContext.current
    ElevatedButton(
        onClick = { context.startActivity(Intent(context, GameOptionsActivity::class.java)) }
    ) {
        Text("Create")
    }
}

@Composable
fun JoinGameButton() {
    val context = LocalContext.current
    ElevatedButton(
        onClick = { context.startActivity(Intent(context, LobbyListActivity::class.java)) }
    ) {
        Text("Join")
    }
}