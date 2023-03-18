package com.github.freeman.bootcamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class TopicSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                TopicSelectionScreen()
            }
        }
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
fun TopicSelectionScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("TopicSelectionScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Topic Selection Activity to implement")
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

@Preview(showBackground = true)
@Composable
fun TopicSelectionScreenPreview() {
    TopicSelectionScreen()
}