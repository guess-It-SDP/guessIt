package com.github.freeman.bootcamp

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
import com.github.freeman.bootcamp.TopicSelectionActivity.Companion.SELECT_TOPIC
import com.github.freeman.bootcamp.TopicSelectionActivity.Companion.TOPIC1
import com.github.freeman.bootcamp.TopicSelectionActivity.Companion.TOPIC2
import com.github.freeman.bootcamp.TopicSelectionActivity.Companion.TOPIC3
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
        setContent {
            BootcampComposeTheme {
                TopicSelectionScreen(dbref)
            }
        }
    }

    // TODO: Delete all topic constants and instead fetch random topics from the firebase
    companion object {
        const val SELECT_TOPIC = "Select the topic you wish to draw"
        const val TOPIC1 = "Apple Syrup"
        const val TOPIC2 = "Banana Syrup"
        const val TOPIC3 = "Tomato Syrup – told you it's not a fruit!"
    }
}

@Composable
fun TopicSelectionBackButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("topicSelectionBackButton"),
        onClick = {
            back(context)
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back arrow icon"
        )
    }
}

@Composable
fun TopicButton(dbref: DatabaseReference, topic: String, id: Int) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("topicButton$id"),
        onClick = { selectTopic(context, dbref, topic) }
    ) {
        Text(topic)
    }
}

fun selectTopic(context: Context, dbref: DatabaseReference, topic: String) {
    dbref.child("topic").setValue(topic)
    context.startActivity(Intent(context, DrawingActivity::class.java))
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
        TopicButton(dbref, TOPIC1, 1)
        Spacer(modifier = Modifier.size(20.dp))
        TopicButton(dbref, TOPIC2, 2)
        Spacer(modifier = Modifier.size(20.dp))
        TopicButton(dbref, TOPIC3, 3)
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