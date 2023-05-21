package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.GamesMenuActivity.Companion.GAMES_MENU_TEXT
import com.github.freeman.bootcamp.GamesMenuActivity.Companion.GAMES_MENU_TITLE
import com.github.freeman.bootcamp.GamesMenuActivity.Companion.NOT_CONNECTED_INFO
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateJoinActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import androidx.compose.material3.MaterialTheme

class GamesMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                Surface {
                    Column {
                        TopAppbarGamesMenu()
                        GamesMenuScreen()
                    }
                }
            }
        }
    }

    companion object {
        const val GAMES_MENU_TITLE = "Games Menu"
        const val GAMES_MENU_TEXT = "What game do you want to play?"
        const val NOT_CONNECTED_INFO = "You are not connected to the internet!\nTo play Guess It!, please connect first."
    }
}

@Composable
fun GamesMenuScreen() {
    val context = LocalContext.current
    val isConnectedToInternet = checkInternetConnectivity(context)

    Column (
        modifier = Modifier
            .testTag("gamesMenuScreen")
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {

            Text(
                text = GAMES_MENU_TEXT,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 40.sp
            )

            Spacer(Modifier.size(20.dp))

            GameMenuButton(
                testTag = "guessItButton",
                text = context.getString(R.string.guessit_name),
                iconId = R.drawable.draw,
                onClick = {
                    if (isConnectedToInternet) {
                        context.startActivity(Intent(context, CreateJoinActivity::class.java))
                    } else {
                        Toast.makeText(context, NOT_CONNECTED_INFO, Toast.LENGTH_LONG).show()
                    }
                }
            )

            GameMenuButton(
                testTag = "wordleButton",
                text = context.getString(R.string.wordle_name),
                iconId = R.drawable.abc,
                onClick = {
                    context.startActivity(Intent(context, WordleMenu::class.java))
                }
            )
        }
    )
}

@Composable
fun GameMenuButton(testTag: String, onClick: () -> Unit, text: String, iconId: Int) {
    ElevatedButton(
        modifier= Modifier
            .testTag(testTag)
            .padding(16.dp),
        onClick = onClick,
    ) {
        Row (
            modifier = Modifier
                .padding(5.dp)
        ){
            Image(
                painterResource(id = iconId),
                contentDescription = "game menu button icon",
                modifier = Modifier
                    .testTag("gameMenuButtonIcon")
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = text,
                fontStyle = FontStyle.Italic
            )
        }

    }

}

fun checkInternetConnectivity(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        ?: false
}

@Composable
fun TopAppbarGamesMenu() {
    val context = LocalContext.current

    TopAppBar(
        modifier = Modifier.testTag("topAppbarGamesMenu"),
        title = {
            Text(
                text = GAMES_MENU_TITLE,
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


