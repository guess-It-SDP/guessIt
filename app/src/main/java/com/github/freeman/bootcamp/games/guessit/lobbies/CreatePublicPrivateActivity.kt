package com.github.freeman.bootcamp.games.guessit.lobbies

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity.Companion.PRIVATE_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity.Companion.PUBLIC_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity.Companion.TOPBAR_TEXT
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class CreatePublicPrivateActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                TopAppbarPublicPrivate()

                MainScreen()
            }
        }
    }

    companion object {
        const val TOPBAR_TEXT = "Create a lobby"
        const val PUBLIC_BUTTON_TEXT = "Public lobby"
        const val PRIVATE_BUTTON_TEXT = "Private lobby"
    }
}



@Composable
private fun TopAppbarPublicPrivate(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarPublicPrivate"),
        title = {
            Text(
                text = TOPBAR_TEXT,
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
private fun MainScreen() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .testTag("createPublicPrivateMainScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        PublicLobbyButton()
        Spacer(modifier = Modifier.size(6.dp))
        PrivateLobbyButton()
    }
}

@Composable
private fun PublicLobbyButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("publicLobbyButton"),
        onClick = {
            context.startActivity(
                Intent(context, GameOptionsActivity::class.java)
                    .putExtra("type", "public")
            )
        }
    ) {
        Text(PUBLIC_BUTTON_TEXT)
    }
}

@Composable
private fun PrivateLobbyButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("privateLobbyButton"),
        onClick = {
            context.startActivity(
                Intent(context, GameOptionsActivity::class.java)
                    .putExtra("type", "private")
            )
        }
    ) {
        Text(PRIVATE_BUTTON_TEXT)
    }
}