package com.github.freeman.bootcamp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.TopAppbarDisplayRecaps
import com.github.freeman.bootcamp.games.guessit.VideoGallery
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class DisplayRecapsTest {
    @get:Rule
    val composeRule = createComposeRule()

    fun setTheContentBefore(){
        composeRule.setContent {
            BootcampComposeTheme {
                MainMenuScreen()
            }
        }

        composeRule.onNodeWithTag("displayRecapsButton").performClick()
    }

    @Test
    fun topAppBarIsDisplayed() {
        setTheContentBefore()
        composeRule.onNodeWithTag("topAppbarDisplayRecaps").assertIsDisplayed()
    }

    @Test
    fun videoGalleryIsDisplayed() {

        composeRule.setContent {
            val context = LocalContext.current

            // Access the resources object from the context
            val resources = context.resources

            // Get the resource file as an InputStream
            val inputStream: InputStream = resources.openRawResource(R.raw.recap_test_game_id)

            // Create a new File object to store the file
            val file = File(context.cacheDir, "recap_test_game_id.mp4")

            // Use buffered streams to efficiently copy the file
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            BootcampComposeTheme {
                Surface (modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxSize()) {
                    Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                        TopAppbarDisplayRecaps()
                        VideoGallery(videos = arrayOf(file))
                    }
                }
            }
        }

        composeRule.onNodeWithTag("videoGallery").assertIsDisplayed()
    }
}