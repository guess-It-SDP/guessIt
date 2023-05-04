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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity.Companion.CREATE_GAME_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity.Companion.JOINING_GAME_BUTTON_TEXT
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity.Companion.TOPBAR_TEXT
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
                        .testTag(getString(R.string.createjoin_screen)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Text(
                        modifier = Modifier
                            .testTag(getString(R.string.createjoin_text1)),
                        text = SCREEN_TEXT_1,
                        fontSize = 30.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.size(60.dp))
                    Text(
                        modifier = Modifier
                            .testTag(getString(R.string.createjoin_text2)),
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
                    contentDescription = context.getString(R.string.createjoin_topbar_icon),
                )
            }
        }
    )
}

@Composable
fun CreateGameButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag(context.getString(R.string.createjoin_creategame)),
        onClick = { context.startActivity(Intent(context, CreatePublicPrivateActivity::class.java)) }
    ) {
        Text(CREATE_GAME_BUTTON_TEXT)
    }
}

@Composable
fun JoinGameButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag(context.getString(R.string.createjoin_joininggame)),
        onClick = { context.startActivity(Intent(context, LobbyListActivity::class.java)) }
    ) {
        Text(JOINING_GAME_BUTTON_TEXT)
    }
}