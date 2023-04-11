package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class WaitingRoomActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContent {
            BootcampComposeTheme {
                Column {
                    TopAppbarWaitingRoom()
                }
            }
        }
    }
}

@Composable
fun TopAppbarWaitingRoom(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarWaitingRoom"),
        title = {
            Text(
                text = "Waiting Room",
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