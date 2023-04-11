package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.media.AsyncPlayer
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class LobbyListActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BootcampComposeTheme {
                Column {
                    TopAppbarLobbies()

                    Surface(
                        modifier = Modifier.fillMaxSize(), color = Color(0xFFF1F1F1)
                    ) {
                        val lobbyList = listOf(
                            Lobby("test lobby", 0, 0),
                            Lobby("test lobby2", 0, 0),
                            Lobby("test lobby3", 0, 0),
                            Lobby("test lobby3", 0, 0),
                            Lobby("test lobby3", 0, 0),
                            Lobby("test lobby3", 0, 0),
                            Lobby("test lobby3", 0, 0),
                            Lobby("test lobby3", 0, 0),
                            Lobby("test lobby3", 0, 0),
                            Lobby("test lobby3", 0, 0),
                        )
                        LobbyList(lobbyList)
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppbarLobbies(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarLobbies"),
        title = {
            Text(
                text = "Lobbies",
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
fun ListItem(
    modifier: Modifier = Modifier,
    lobby: Lobby = Lobby("default lobby", 0, 0),
    backgroundColor: Color = Color.LightGray,
    onItemClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .clickable { onItemClick() }
            .padding(8.dp)
            .background(backgroundColor)

    ) {
        Row(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Column(
            ) {
                Text(
                    text = lobby.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${lobby.nbRounds} rounds",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "players : ${lobby.nbPlayer}/5",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )

        }
    }
}

@Composable
fun LobbyList(lobbies: List<Lobby>) {
    var lobbyList = remember {
        mutableStateListOf<Lobby>()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn {
            items(lobbies) { lobby ->
                ListItem(
                    lobby = lobby,
                    backgroundColor = Color.White,
                    onItemClick = {
                        Log.i("LobbyList", "Info $lobby")
                    },
                )
            }
        }
    }
}

data class Lobby(val name: String, val nbPlayer: Int, val nbRounds: Int)