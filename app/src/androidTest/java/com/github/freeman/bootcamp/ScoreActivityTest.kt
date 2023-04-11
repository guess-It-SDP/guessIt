package com.github.freeman.bootcamp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.ScoreActivity.Companion.SCORES_TITLE
import com.github.freeman.bootcamp.games.guessit.ScoreActivity.Companion.gameEnded
import com.github.freeman.bootcamp.games.guessit.ScoreScreen
import com.github.freeman.bootcamp.games.guessit.reinitialise
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScoreActivityTest {

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
        gameEnded = false
        composeRule.setContent {
            val gameId = "TestGameId"
            val dbRef = Firebase.database.getReference("Games/$gameId")
            reinitialise(dbRef, playerIds.toSet())

           initFirebaseScores(dbRef, playerIds, scores)
           val playersToScores = initPlayersToScores(playerIds, scores)
           val usersToScores = usersToScoresToPair(playerIds, usernames, scores)

            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScoreScreen(dbRef = dbRef, testingPlayersToScores = playersToScores,
                        testingUsersToScores = usersToScores)
                }
            }
        }
    }

    @Test
    fun scoreboardIsDisplayed() {
        composeRule.onNodeWithTag("scoreboard").assertIsDisplayed()
    }

    @Test
    fun scoresTitleIsDisplayed() {
        composeRule.onNodeWithTag("scoresTitle").assertIsDisplayed()
    }

    @Test
    fun scoresTitleIsCorrect() {
        composeRule.onNodeWithTag("scoresTitle").assertTextContains(SCORES_TITLE)
    }

    @Test
    fun usernamesAreDisplayed() {
        for (username in usernames.values) {
            composeRule.onNodeWithTag(username).assertIsDisplayed()
        }
    }

    @Test
    fun usernamesTextIsCorrect() {
        for (username in usernames.values) {
            composeRule.onNodeWithTag(username).assertTextContains(username)
        }
    }

    @Test
    fun scoresAreDisplayed() {
        composeRule.onAllNodesWithTag("score").apply {
            fetchSemanticsNodes().forEachIndexed {
                i, _ -> get(i).assertIsDisplayed()
            }
        }
    }


}

// Initialises the player scores of the test game on the Firebase
fun initFirebaseScores(dbRef: DatabaseReference, playerIds: List<String>, scores: Map<String, Int>) {
    for (id in playerIds) {
        val dbScoreRef = dbRef.child("Players/$id/score")
        FirebaseUtilities.databaseGetLong(dbScoreRef)
            .thenAccept {
                dbScoreRef.setValue(scores[id])
            }
    }
}

// Initialises the values of the playerID-to-score map
@Composable
fun initPlayersToScores(playerIds: List<String>, scores: Map<String, Int>): HashMap<String, MutableState<Int>> {
    val playersToScores = HashMap<String, MutableState<Int>>()
    for (id in playerIds) {
        playersToScores[id] = remember { mutableStateOf(-1) }
    }
    for (id in playerIds) {
        playersToScores[id]?.value = scores[id]!!
    }
    return playersToScores
}

// Transforms the usernames-to-scores map to the required format
fun usersToScoresToPair(playerIds: List<String>, usernames: Map<String, String>, scores: Map<String, Int>): ArrayList<Pair<String?, Int>> {
    val usersToScores = ArrayList<Pair<String?, Int>>()
    for (id in playerIds) {
        usersToScores.add(Pair(usernames[id], scores[id]) as Pair<String?, Int>)
    }
    return usersToScores
}