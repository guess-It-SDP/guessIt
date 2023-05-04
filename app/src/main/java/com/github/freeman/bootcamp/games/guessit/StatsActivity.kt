package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.SettingsActivity
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.STATS_TITLE

class StatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

        }
    }

    companion object {
        const val STATS_TITLE = "Statistics"
    }
}

@Composable
fun TopAppbarStats(context: Context = LocalContext.current) {
    TopAppBar(
        modifier = Modifier.testTag("topAppbarProfile"),
        title = {
            Text(
                text = STATS_TITLE,
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

