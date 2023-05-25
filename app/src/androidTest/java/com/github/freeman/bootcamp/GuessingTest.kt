package com.github.freeman.bootcamp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.SCREEN_TEXT
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity.Companion.answer
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingScreen
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuessingTest {
    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    @Before
    fun initScreenWithDatabase() {
        FirebaseEmulator.init()
        val guessGameId = "GameTestGuessesId"
        val database = FirebaseSingletons.database.get().database.getReference("games/$guessGameId/guesses")

        composeRule.setContent {
            val context = LocalContext.current
            val guessGameId = context.getString(R.string.test_game_id)
            val database = FirebaseSingletons.database.get().database.reference
                .child(context.getString(R.string.games_path))
                .child(guessGameId)
                .child(context.getString(R.string.guesses_path))
            val storageGamRef = Firebase.storage.reference
                .child(context.getString(R.string.game_recaps_path))
                .child(context.getString(R.string.test_game_id))

            BootcampComposeTheme {
                GuessingScreen(database, context = context, storageGamRef, LocalLifecycleOwner.current)
            }
        }
    }

    @Test
    fun guessingScreenIsDisplayed() {
        composeRule.onNodeWithTag("guessingScreen").assertIsDisplayed()
    }

    @Test
    fun guessingScreenContainsCorrectText() {
        composeRule.onNodeWithTag("guessText").assertTextContains(SCREEN_TEXT)
    }

    @Test
    fun guessesListIsDisplayed() {
        composeRule.onNodeWithTag("guessesList").assertIsDisplayed()
    }

    @Test
    fun guessingBarIsDisplayed() {
        composeRule.onNodeWithTag("guessingBar").assertIsDisplayed()
    }

    @Test
    fun guessingPreviewDisplaysGuessingScreen() {
        composeRule.onNodeWithTag("guessingScreen").assertIsDisplayed()
        composeRule.onNodeWithTag("guessText").assertTextContains(SCREEN_TEXT)
        composeRule.onNodeWithTag("guessesList").assertIsDisplayed()
        composeRule.onNodeWithTag("guessingBar").assertIsDisplayed()
    }

    @Test
    fun guessIsDisplayedInGuessingList() {
        composeRule.onNode(hasSetTextAction()).performTextInput("House")
        composeRule.onNodeWithTag("guessButton").performClick()
    }

    @Test
    fun popupIsDisplayedUponGuessingCorrectly() {
        composeRule.onNode(hasSetTextAction()).performTextInput(answer)
        composeRule.onNodeWithTag("guessButton").performClick()
        composeRule.onNodeWithTag("popUpScreen").assertIsDisplayed()
    }
}