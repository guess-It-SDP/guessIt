package com.github.freeman.bootcamp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.EndScoreboard
import com.github.freeman.bootcamp.games.guessit.ScoreActivity
import com.github.freeman.bootcamp.games.guessit.reinitialise
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.getGameDBRef
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndScoreboardTest {

    /* Note: In order for these tests to pass, "Games/TestGameId/Players" needs to contain the
      following IDs: 2F2gnvJ21CiEakYxlV2n82iOMelSU2 and gXKYlNYlCXW3qUGPXYn0KL0PBij2. These IDs
      are linked to the profiles having the usernames Dan and Imposter respectively. */
    private val playerIds = listOf("2gnvJ21CiEakYxlV2n82iOMelSU2", "gXKYlNYlCXW3qUGPXYn0KL0PBij2")
    private val usernames = mapOf(playerIds[0] to "Dan", playerIds[1] to "Imposter")
    private val scores = mapOf(playerIds[0] to 3, playerIds[1] to 5)

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            val context = LocalContext.current
            val dbRef = getGameDBRef(context)
            reinitialise(context, dbRef, playerIds.toSet())

            initFirebaseScores(context, dbRef, playerIds, scores)
            val usersToScores = usersToScoresToPair(playerIds, usernames, scores)

            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EndScoreboard(usersToScores = usersToScores)
                }
            }
        }
    }

    @Test
    fun endScoreboardIsDisplayed() {
        composeRule.onNodeWithTag("endScoreboard").assertIsDisplayed()
    }

    @Test
    fun endScoresTitleIsDisplayed() {
        composeRule.onNodeWithTag("endScoresTitle").assertIsDisplayed()
    }

    @Test
    fun endScoresTitleIsCorrect() {
        composeRule.onNodeWithTag("endScoresTitle").assertTextContains(ScoreActivity.FINAL_SCORES_TITLE)
    }

    @Test
    fun winnerTextIsDisplayed() {
        composeRule.onNodeWithTag("winnerDeclaration").assertIsDisplayed()
    }

    @Test
    fun endScoresAreDisplayed() {
        composeRule.onAllNodesWithTag("endScore").apply {
            fetchSemanticsNodes().forEachIndexed {
                    i, _ -> get(i).assertIsDisplayed()
            }
        }
    }

    @Test
    fun scoresAreCorrect() {
        composeRule.onAllNodesWithTag("endScore").apply {
            fetchSemanticsNodes().forEachIndexed {
                    i, _ -> get(i).assertTextContains(scores[playerIds[i]]!!.toString())
            }
        }
    }

    @Test
    fun usernamesAreCorrect() {
        for (name in usernames.values) {
            composeRule.onNodeWithTag("end$name").assertTextContains(name)
        }
    }
}