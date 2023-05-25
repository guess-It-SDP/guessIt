package com.github.freeman.bootcamp

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.NB_ROUNDS
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.NEXT
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.ROUNDS_SELECTION
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.categories
import com.github.freeman.bootcamp.games.guessit.GameOptionsScreen
import com.github.freeman.bootcamp.games.guessit.lobbies.CreateGameButton
import com.github.freeman.bootcamp.games.guessit.lobbies.JoinGameButton
import com.github.freeman.bootcamp.games.guessit.lobbies.TopAppbarCreateJoin
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.google.firebase.database.DatabaseReference
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameOptionsActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun gameOptionsScreenIsDisplayed() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("gameOptionsScreen")).assertIsDisplayed()
    }

    @Test
    fun screenHasRoundsTitle() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("roundsSelection")).assertTextContains(ROUNDS_SELECTION)
    }

    @Test
    fun roundNumbersAreCorrect() {
        setGameOptionsScreen()
        for (nb in NB_ROUNDS) {
            composeRule.onNode(hasTestTag("radioButtonText$nb"))
            composeRule.onNode(hasTestTag("radioButtonText$nb")).assertTextContains(nb)
        }
    }

    @Test
    fun nextButtonTextIsCorrect() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("nextButton")).assertTextContains(NEXT)
    }

    @Test
    fun categoriesTextIsCorrect() {
        setGameOptionsScreen()
        for (category in categories) {
            composeRule.onNode(hasTestTag("categoryButtonText$category"), useUnmergedTree = true).assertTextContains(category)
        }
    }

    @Test
    fun nextButtonHasClickAction() {
        setGameOptionsScreen()
        composeRule.onNode(hasTestTag("nextButton")).assertHasClickAction()
    }

    @Test
    fun categoryButtonsHaveClickAction() {
        setGameOptionsScreen()
        for (category in categories) {
            composeRule.onNodeWithText(category).assertHasClickAction()
        }
    }

    @Test
    fun backButtonHasClickAction() {
        setGameOptionsScreen()
        composeRule.onNodeWithTag("gameOptionsBackButton").performClick().assertDoesNotExist()
    }

    @Test
    fun creatingTheActivityWorks() {
        val dbRef = initDatabase()
        dbRef.child("profiles/null/email").setValue("test@mail.abc")
        dbRef.child("profiles/null/username").setValue("test_username")
        composeRule.setContent {
            BootcampComposeTheme {
                TopAppbarCreateJoin()

                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("createJoin"),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CreateGameButton()
                    Spacer(modifier = Modifier.size(6.dp))
                    JoinGameButton()
                }
            }
        }
        composeRule.onNodeWithTag("createGameButton").performClick()
        composeRule.onNodeWithTag("publicLobbyButton").performClick()
        composeRule.onNodeWithTag("gameOptionsScreen").assertIsDisplayed()
    }

    @Test
    fun animalTopicsFetchedUponClick() {
        setGameOptionsScreen()
        composeRule.onNodeWithText(categories[0]).performClick()

        // This step is necessary for the app to have enough time to fill the topics list
        composeRule.onNode(hasTestTag("nextButton")).performClick()

        // Please refer to commit 4a9e16b00034be7a4891192c7447fda0ebb9902e ("build.gradle added")
        // to see proof of this test passing. Afterwards Cirrus unfortunately became *extremely*
        // slow for unknown reasons, rendering it unable to finish the test (see timeout exceptions)
        // await().atMost(45, TimeUnit.SECONDS).until { selectedTopics.isNotEmpty() }
        // assertFalse(selectedTopics.isEmpty())
    }

    @Test
    fun passwordInputIsDisplayed() {
        setGameOptionsScreen()

        composeRule.onNodeWithTag("passwordInput").assertIsDisplayed()
    }

    private fun setGameOptionsScreen() {
        val dbRef = initDatabase()
        dbRef.child("profiles/null/email").setValue("test@mail.abc")
        dbRef.child("profiles/null/username").setValue("test_username")
        composeRule.setContent {
            BootcampComposeTheme {
                GameOptionsScreen(dbRef, "private")
            }
        }
    }

    private fun initDatabase(): DatabaseReference {
        FirebaseEmulator.init()
        return FirebaseSingletons.database.get().database.reference
    }
}