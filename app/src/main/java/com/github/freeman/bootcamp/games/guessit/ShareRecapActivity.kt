package com.github.freeman.bootcamp.games.guessit

import com.github.freeman.bootcamp.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem.fromUri
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.github.freeman.bootcamp.games.guessit.ShareRecapActivity.Companion.SHARE_RECAP_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.ui.theme.Pink40
import com.github.freeman.bootcamp.ui.theme.Pink80
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ShareRecapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId = intent.getStringExtra(getString(R.string.gameId_extra)).toString()

        setContent {
            BootcampComposeTheme {
                ShareRecapScreen(gameId)
            }
        }
    }

    companion object {
        const val SHARE_RECAP_TITLE = "Share the recap of your game with your friends!"
    }
}

@Composable
fun ShareRecapScreen(gameId:String) {
    val context = LocalContext.current

    val videoStorageRef = Firebase.storage.reference
        .child(context.getString(R.string.game_recaps_path))
        .child(gameId)
        .child(context.getString(R.string.recap_path))

    val videoUrl = remember { mutableStateOf("") }

    FirebaseUtilities.urlStorageGet(videoStorageRef)
        .thenAccept {
            videoUrl.value = it.toString()
        }

    Column(
        modifier = Modifier
            .testTag("shareRecapScreen")
            .fillMaxSize()
            .padding(10.dp)
            .background(Pink80),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(20.dp)
                .testTag("shareRecapTitle"),
            text = SHARE_RECAP_TITLE,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        Spacer(Modifier.size(20.dp))
        RecapPreview(videoUrl.value)
        Spacer(Modifier.size(20.dp))
        ShareButton(videoUrl.value)
    }
}

@Composable
fun ShareButton(videoUrl: String) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            // Start a background thread to download and share the video
            Thread {
                try {
                    val videoFile = downloadVideo(videoUrl)
                    shareVideo(context, videoFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        },
        modifier = Modifier
            .testTag("shareRecapButton")
            .background(Color.White, CircleShape)
    ) {

        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share Icon",
            modifier = Modifier
                .width(20.dp)
                .height(20.dp),
            tint = Pink80
        )
    }
}

private fun downloadVideo(videoUrl: String): File {
    val url = URL(videoUrl)
    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
    connection.connect()

    val inputStream: InputStream = connection.inputStream
    val videoFile = File.createTempFile("video", ".mp4")
    val fileOutputStream = FileOutputStream(videoFile)

    val buffer = ByteArray(1024)
    var bytesRead: Int

    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        fileOutputStream.write(buffer, 0, bytesRead)
    }

    fileOutputStream.close()
    inputStream.close()

    return videoFile
}

private fun shareVideo(context: Context, videoFile: File) {
    val videoUri: Uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        videoFile
    )

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, videoUri)
        type = "video/mp4"
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
}

@Composable
fun RecapPreview(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .testTag("recapPreview")
            .padding(20.dp)
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                }
            },
            update = { view ->
                val mediaItem = fromUri(videoUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            }
        )
    }
}

@Preview
@Composable
fun ShareButtonPreview() {
    ShareRecapScreen(gameId = "test_game_id")
}
