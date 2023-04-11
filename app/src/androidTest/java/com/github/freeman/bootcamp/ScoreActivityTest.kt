package com.github.freeman.bootcamp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ScoreActivity.Companion.SCORES_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScoreActivityTest {

    /* Note: In order for these tests to pass, "Games/TestGameId/Players" needs to contain the
       following IDs: 2F2gnvJ21CiEakYxlV2n82iOMelSU2 and gXKYlNYlCXW3qUGPXYn0KL0PBij2 which are
       linked to the profiles having the usernames Dan and Imposter respectively. */
    private val playerIds = listOf("2F2gnvJ21CiEakYxlV2n82iOMelSU2", "gXKYlNYlCXW3qUGPXYn0KL0PBij2")
    private val usernames = mapOf(playerIds[0] to "Dan", playerIds[1] to "Imposter")

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            val gameId = "TestGameId"
            val dbRef = Firebase.database.getReference("Games/$gameId/Players")
            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScoreScreen(dbRef = dbRef)
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

//    @Test
//    fun usernamesAreDisplayed() {
//        for (username in usernames.values) {
//            composeRule.onNodeWithTag(username).assertIsDisplayed()
//        }
//    }

//    @Test
//    fun usernamesTextIsCorrect() {
//        for (username in usernames.values) {
//            composeRule.onNodeWithTag(username).assertTextContains(username)
//        }
//    }

    @Test
    fun scoresAreDisplayed() {
        composeRule.onAllNodesWithTag("score").apply {
            fetchSemanticsNodes().forEachIndexed {
                i, _ -> get(i).assertIsDisplayed()
            }
        }
    }
}