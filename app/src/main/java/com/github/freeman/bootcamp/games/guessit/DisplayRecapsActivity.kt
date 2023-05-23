package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import java.io.File

/**
 * Creates an activity where a user can see the game recaps stored in the device
 */
class DisplayRecapsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val videoDir = File(context.getExternalFilesDir(null), context.getString(R.string.game_recaps_local_path))
            val videos = videoDir.listFiles()

            BootcampComposeTheme {
                Surface (modifier = Modifier.background(color = MaterialTheme.colorScheme.background).fillMaxSize()) {
                    Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                        TopAppbarDisplayRecaps()
                        if (videos != null) {
                            Box(modifier = Modifier.padding(5.dp).background(color = MaterialTheme.colorScheme.background)) {
                                VideoGallery(videos = videos)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppbarDisplayRecaps(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarDisplayRecaps"),
        title = {
            Text(
                text = "Recaps",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                val activity = (context as? Activity)
                activity?.finish()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoGallery(videos: Array<File>?) {
    val pagerState = rememberPagerState()

    VerticalPager(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background),
        pageCount = videos!!.size,
        state = pagerState
    ) { page ->
        if (page < videos.size) {
            val videoFile = videos[page]
            val videoUri = videoFile.absolutePath
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                RecapPreview(
                    videoUrl = videoUri
                )
            }

        }
    }
}
