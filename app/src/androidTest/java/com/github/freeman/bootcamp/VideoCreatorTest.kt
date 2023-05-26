package com.github.freeman.bootcamp

import android.content.Context
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.ShareRecapActivity
import com.github.freeman.bootcamp.games.guessit.ShareRecapScreen
import com.github.freeman.bootcamp.games.guessit.VideoCreator.Companion.createRecap
import com.github.freeman.bootcamp.games.guessit.chat.ChatScreen
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons.database
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.storage.ktx.storage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoCreatorTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            val context = LocalContext.current
            val gameId = "test_game_id"
            createRecap(context, gameId)

            BootcampComposeTheme {
                Surface {
                    ShareRecapScreen(gameId)
                }
            }

        }
    }

    @Test
    fun shareRecapScreenIsDisplayedWithAllComposable() {
        composeRule.onNodeWithTag("shareRecapBackButton").assertIsDisplayed()
        composeRule.onNodeWithTag("shareRecapBackButton").assertHasClickAction()
        composeRule.onNodeWithTag("shareRecapScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("shareRecapTitle").assertIsDisplayed()
        composeRule.onNodeWithTag("shareRecapTitle").assertTextContains(ShareRecapActivity.SHARE_RECAP_TITLE)
        composeRule.onNodeWithTag("recapPreview").assertIsDisplayed()
    }
}