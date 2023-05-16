package com.github.freeman.bootcamp.games.guessit

import android.R
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview


class ShareRecapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

@Composable
fun ShareButton() {
    val context = LocalContext.current

    IconButton(onClick = {
        // finding videoview by its id
        // finding videoview by its id
        val videoView: VideoView = findViewById(R.id.videoView)

        // Uri object to refer the
        // resource from the videoUrl

        // Uri object to refer the
        // resource from the videoUrl
        val uri = Uri.parse(videoUrl)

        // sets the resource from the
        // videoUrl to the videoView

        // sets the resource from the
        // videoUrl to the videoView
        videoView.setVideoURI(uri)

        // creating object of
        // media controller class

        // creating object of
        // media controller class
        val mediaController = MediaController(this)

        // sets the anchor view
        // anchor view for the videoView

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(videoView)

        // sets the media player to the videoView

        // sets the media player to the videoView
        mediaController.setMediaPlayer(videoView)

        // sets the media controller to the videoView

        // sets the media controller to the videoView
        videoView.setMediaController(mediaController)

        // starts the video

        // starts the video
        videoView.start()
    }) {
        Icon(Icons.Filled.Share, contentDescription = "Share")
    }

}

@Preview
@Composable
fun ShareButtonPreview() {
    ShareButton()
}
