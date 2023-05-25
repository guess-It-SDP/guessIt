package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.NB_TOPICS
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity.Companion.SELECT_TOPIC
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity.Companion.roundNb
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity.Companion.topics
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity.Companion.turnNb
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.databaseGet
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.databaseGetList
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TopicSelectionActivity : ComponentActivity() {
    private lateinit var dbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()
        dbref = getGameDBRef(this, gameId)

        // inform database that a player is in the topic selection phase
        dbref.child(getString(R.string.current_state_path))
            .setValue(getString(R.string.state_topicselection))

        setContent {
            BootcampComposeTheme {
                TopicSelectionScreen(dbref, gameId)
            }
        }
    }

    companion object {
        const val SELECT_TOPIC = "Select the topic you wish to draw"
        var topics = mutableListOf("Topic1", "Topic2", "Topic3")
        var roundNb = 0
        var turnNb = 0
    }
}


private fun refreshTopics(context: Context, dbref: DatabaseReference, topicList: List<MutableState<String>>) {
    databaseGet(dbref.child(context.getString(R.string.param_category_path)))
        .thenAccept { category ->

            val topicsRef = Firebase.database.reference
                .child(context.getString(R.string.topics_path))
                .child(category)
            databaseGetList(topicsRef)
                .thenAccept {
                    topics.clear()

                    for (topic in topicList) {
                        val newTopic = it.random() as String
                        topics.add(newTopic)
                        topic.value = newTopic
                    }
                }
        }
}

@Composable
fun TopicSelectionBackButton(dbref: DatabaseReference) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("topicSelectionBackButton"),
        onClick = {
            backToWaitingRoom(dbref, context)
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back arrow icon"
        )
    }
}

fun backToWaitingRoom(dbref: DatabaseReference, context: Context) {
    dbref.child(context.getString(R.string.current_state_path))
        .setValue(context.getString(R.string.state_waitingforplayers))
    val activity = (context as? Activity)
    activity?.finish()
}

@Composable
fun TopicButton(dbref: DatabaseReference, topic: MutableState<String>, id: Int, gameId: String) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("topicButton$id"),
        onClick = {
            selectTopic(context, dbref, topic.value, gameId)
        }
    ) {
        Text(topic.value)
    }
}

fun selectTopic(context: Context, dbref: DatabaseReference, topic: String, gameId: String) {
    dbref.child(context.getString(R.string.topics_path))
        .child(roundNb.toString())
        .child(turnNb.toString())
        .child(context.getString(R.string.topic_path))
        .setValue(topic)
    dbref.child(context.getString(R.string.current_round_path))
        .setValue(roundNb)
    dbref.child(context.getString(R.string.current_turn_path))
        .setValue(turnNb)
    dbref.child(context.getString(R.string.current_timer_path))
        .setValue(context.getString(R.string.timer_inprogress))
    dbref.child(context.getString(R.string.current_state_path))
        .setValue(context.getString(R.string.state_playturn))

    context.startActivity(Intent(context, DrawingActivity::class.java).apply {
        putExtra(context.getString(R.string.gameId_extra), gameId)
    })
    val activity = (context as? Activity)
    activity?.finish()
}

@Composable
fun TopicSelectionScreen(dbref: DatabaseReference, gameId: String) {
    val context = LocalContext.current

    //the current round and turn (in the round)
    databaseGet(dbref.child(context.getString(R.string.current_round_path)))
        .thenAccept {
            roundNb = it.toInt()
        }
    databaseGet(dbref.child(context.getString(R.string.current_turn_path)))
        .thenAccept {
            turnNb = it.toInt()
        }

    val topic0 = remember { mutableStateOf(topics[0]) }
    val topic1 = remember { mutableStateOf(topics[1]) }
    val topic2 = remember { mutableStateOf(topics[2]) }
    val topicList = listOf(topic0, topic1, topic2)
    refreshTopics(context, dbref, topicList)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("topicSelectionScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.testTag("topicSelection"),
            text = SELECT_TOPIC
        )
        Spacer(modifier = Modifier.size(40.dp))
        TopicButton(dbref, topic0, 1, gameId)
        Spacer(modifier = Modifier.size(20.dp))
        TopicButton(dbref, topic1, 2, gameId)
        Spacer(modifier = Modifier.size(20.dp))
        TopicButton(dbref, topic2, 3, gameId)

        // refresh button
        IconButton(
            modifier = Modifier
                .weight(weight = 1f, fill = false)
                .testTag("refreshButton"),
            onClick = {
                refreshTopics(context, dbref, topicList)
            }) {
            androidx.compose.material.Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Edit Details",
                tint = MaterialTheme.colors.primary
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TopicSelectionBackButton(dbref)
    }
}
