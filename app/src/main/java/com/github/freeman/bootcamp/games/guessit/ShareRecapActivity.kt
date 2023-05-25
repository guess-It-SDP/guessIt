package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.ShareRecapActivity.Companion.SHARE_RECAP_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.ui.theme.md_theme_light_inversePrimary
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.database.DatabaseReference
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
                Surface {
                    ShareRecapScreen(gameId)
                }
            }
        }
    }

    companion object {
        const val SHARE_RECAP_TITLE = "Share the recap of your game with your friends!"
    }
}

/**
 * The screen for sharing the recap of the game players have just played, containing
 * a recap preview and the sharing button
 * @param gameId The id of the game
 */
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

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            ShareRecapBackButton()
        }

        Column(
            modifier = Modifier
                .testTag("shareRecapScreen")
                .fillMaxSize()
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .testTag("shareRecapTitle"),
                text = SHARE_RECAP_TITLE,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 35.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            RecapPreview(videoUrl.value)
            ShareButton(videoUrl.value)
        }
    }
}

/**
 * The button to back to the final score board
 */
@Composable
fun ShareRecapBackButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier
            .testTag("shareRecapBackButton"),
        onClick = {
            val activity = (context as? Activity)
            activity?.finish()
        },
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back arrow icon",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * The button to share the recap
 * @param videoUrl The URL of the recap video
 */
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
            .background(MaterialTheme.colorScheme.onPrimaryContainer, CircleShape)
    ) {

        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share Icon",
            modifier = Modifier
                .width(20.dp)
                .height(20.dp),
            tint = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

/**
 * Download locally and temporarily the video recap
 * @param videoUrl The URL of the recap video
 */
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

/**
 * Share the video
 */
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

/**
 * The preview of the recap video
 * @param videoUrl The URL of the game recap video
 */
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
            .padding(50.dp)
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
