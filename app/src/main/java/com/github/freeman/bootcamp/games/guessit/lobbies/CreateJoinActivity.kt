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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
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
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity.Companion.CREATE_GAME_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity.Companion.JOINING_GAME_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity.Companion.TOPBAR_TEXT
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.github.freeman.bootcamp.MainMenuButton


/**
 * Shows a screen where you can either join or create a Guess It game
 */
class CreateJoinActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                Surface {
                    TopAppbarCreateJoin()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(getString(R.string.createjoin_screen)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            modifier = Modifier
                                .testTag(getString(R.string.createjoin_text1)),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            text = SCREEN_TEXT_1,
                            fontSize = 30.sp,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.size(60.dp))
                        Text(
                            modifier = Modifier
                                .testTag(getString(R.string.createjoin_text2)),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 20.sp,
                            text = SCREEN_TEXT_2,
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

    companion object {
        const val SCREEN_TEXT_1 = "Let's have fun!"
        const val SCREEN_TEXT_2 = "What do you want to do?"
        const val TOPBAR_TEXT = "Guess It!"
        const val CREATE_GAME_BUTTON_TEXT = "Create a new game"
        const val JOINING_GAME_BUTTON_TEXT = "Join an existing game"
    }
}

@Composable
fun TopAppbarCreateJoin(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag(context.getString(R.string.createjoin_topbar)),
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
                    .testTag("appBarBack"),
                onClick = {
                    val activity = (context as? Activity)
                    activity?.finish()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.primary,
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
    MainMenuButton(
        testTag = "createGameButton",
        onClick = { context.startActivity(Intent(context, CreatePublicPrivateActivity::class.java)) },
        text = CREATE_GAME_BUTTON_TEXT,
        icon = Icons.Default.Edit
    )

}

@Composable
fun JoinGameButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "joinGameButton",
        onClick = { context.startActivity(Intent(context, LobbyListActivity::class.java)) },
        text = JOINING_GAME_BUTTON_TEXT,
        icon = ImageVector.vectorResource(R.drawable.join)
    )
}