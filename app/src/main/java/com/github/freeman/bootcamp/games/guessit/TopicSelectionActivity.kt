package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.NB_TOPICS
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity.Companion.SELECT_TOPIC
import com.github.freeman.bootcamp.games.guessit.TopicSelectionActivity.Companion.topics
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TopicSelectionActivity : ComponentActivity() {
    private lateinit var dbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.getStringExtra("gameId").toString()
        dbref = Firebase.database.getReference("Games/$gameId")
        topics.clear()
        for (i in 0 until NB_TOPICS) {
            topics.add(intent.getStringExtra("topic$i").toString())
        }
        setContent {
            BootcampComposeTheme {
                TopicSelectionScreen(dbref)
            }
        }
    }

    companion object {
        const val SELECT_TOPIC = "Select the topic you wish to draw"
        var topics = mutableListOf<String>("Topic1", "Topic2", "Topic3")
    }
}

@Composable
fun TopicSelectionBackButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("topicSelectionBackButton"),
        onClick = {
            backToGameOptions(context)
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back arrow icon"
        )
    }
}

fun backToGameOptions(context: Context) {
    val intent = Intent(context, GameOptionsActivity::class.java)
    context.startActivity(intent)
    val activity = (context as? Activity)
    activity?.finish()
}

@Composable
fun TopicButton(dbref: DatabaseReference, topic: String, id: Int) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("topicButton$id"),
        onClick = {
            selectTopic(context, dbref, topic)
        }
    ) {
        Text(topic)
    }
}

fun selectTopic(context: Context, dbref: DatabaseReference, topic: String) {
    dbref.child("topic").setValue(topic)
    context.startActivity(Intent(context, DrawingActivity::class.java).apply {
        putExtra("topic", topic)
    })
}

@Composable
fun TopicSelectionScreen(dbref: DatabaseReference) {
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
        TopicButton(dbref, topics[0], 1)
        Spacer(modifier = Modifier.size(20.dp))
        TopicButton(dbref, topics[1], 2)
        Spacer(modifier = Modifier.size(20.dp))
        TopicButton(dbref, topics[2], 3)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TopicSelectionBackButton()
    }
}