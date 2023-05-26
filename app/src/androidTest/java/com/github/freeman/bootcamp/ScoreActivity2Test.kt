package com.github.freeman.bootcamp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.*
import com.github.freeman.bootcamp.games.guessit.FinalActivity.Companion.GAME_OVER
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.videocall.VideoScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScoreActivity2Test {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            val context = LocalContext.current
            val dbRef = FirebaseUtilities.getGameDBRef(context)

            BootcampComposeTheme() {
                val context = LocalContext.current

                // Get the Ids of all players in this game
                val playerIds = remember { mutableStateOf(mapOf<String, Map<String, Int>>()) }
                FirebaseUtilities.databaseGetMap(dbRef.child(context.getString(R.string.players_path)))
                    .thenAccept {
                        playerIds.value = it as HashMap<String, Map<String, Int>>
                    }

                // Get the player ID to score map
                val playersToScores = obtainPlayersToScores(dbRef, playerIds, context)

                val scores = turnIntoPairs(playersToScores)
                val usernames = fetchUserNames(scores)
                val usersToScores = usernamesToScores(
                    scores, usernames
                ).sortedWith(compareByDescending { it.second })


                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CurrentScoreboard(usersToScores = usersToScores)
                    VideoScreen(roomName = "test", testing = false)
                }
            }
        }
    }

    @Test
    fun endScoreboardIsDisplayed() {
        composeRule.onNodeWithTag(ScoreActivity2.SCORES_RECAP_BOARD_TAG).assertIsDisplayed()
    }


    @Test
    fun videoExist() {
        composeRule.onNodeWithTag("agora_video_view").assertExists()
    }
    
}