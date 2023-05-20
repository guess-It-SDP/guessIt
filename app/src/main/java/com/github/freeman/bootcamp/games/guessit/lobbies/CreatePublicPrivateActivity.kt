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
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity.Companion.PRIVATE_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity.Companion.PRIVATE_TYPE_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity.Companion.PUBLIC_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity.Companion.PUBLIC_TYPE_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreatePublicPrivateActivity.Companion.TOPBAR_TEXT
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon

/**
 * Shows a screen where you can either choose to create a public or a private lobby
 */
class CreatePublicPrivateActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                Surface {
                    TopAppbarPublicPrivate()
                    PublicPrivateLobbyScreen()
                }
            }
        }
    }

    companion object {
        const val TOPBAR_TEXT = "Create a lobby"
        const val PUBLIC_BUTTON_TEXT = "Public lobby"
        const val PRIVATE_BUTTON_TEXT = "Private lobby"
        const val PUBLIC_TYPE_TEXT = "public"
        const val PRIVATE_TYPE_TEXT = "private"
    }
}

@Composable
fun TopAppbarPublicPrivate(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarPublicPrivate"),
        title = {
            Text(
                text = TOPBAR_TEXT,
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
            IconButton(
                modifier = Modifier
                    .testTag("topAppbarPublicPrivateButton"),
                onClick = {
                    val activity = (context as? Activity)
                    activity?.finish()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                )
            }
        }
    )
}

@Composable
fun PublicPrivateLobbyScreen() {
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
                    .putExtra(context.getString(R.string.type_extra), PUBLIC_TYPE_TEXT)
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
                    .putExtra(context.getString(R.string.type_extra), PRIVATE_TYPE_TEXT)
            )
        }
    ) {
        Text(PRIVATE_BUTTON_TEXT)
    }
}