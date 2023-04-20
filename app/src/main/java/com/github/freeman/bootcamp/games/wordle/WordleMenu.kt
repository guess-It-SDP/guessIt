package com.github.freeman.bootcamp.games.wordle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.github.freeman.bootcamp.MainMenuActivity
import com.github.freeman.bootcamp.MainMenuButton
import com.github.freeman.bootcamp.MainMenuScreen
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity
import com.github.freeman.bootcamp.play
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme


/**
 * The is the class that allow the play to the wordle game.
 * This is and adapter from WordleGameState to android
 */
class WordleMenu() : ComponentActivity() {
    companion object {

        /**
         * enum corresponding to each difficulty levels of the game
         *
         * @param prettyText A nice text for display be careful not to confound with the difficulty level string name
         */
        enum class Difficulty(val prettyText: String) {
            EASY("Easy"), MEDIUM("Medium"), HARD("Hard"), VERY_HARD("Very Hard"), VERY_VERY_HARD("Very Very Hard")
        }

        const val LETTERS = " (letters)"
        const val WORD_ONLY = " (words only)"
        const val GAME_RULES = "Rules of the Game"
        const val WORDLE_MENU_TEST_TAG = "WordleMenuScreen"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                WordleMenuScreen()
            }
        }
    }


}

/**
 * Change the activity to the game activity and put the difficulty strong in the extras
 */
private fun launchGame(context: Context, difficulty: String) {
    context.startActivity(Intent(context, WordleGameActivity::class.java).apply {
        putExtra(WordleMenu.Companion.Difficulty::class.simpleName, difficulty)
    })
}

/**
 * Creates a button ot launch a game
 *
 *@param testTag Tag use to find the object for testing
 *@param activityLauncher a unit function to launch next scene
 *@param text The text displayed inside the button
 */
@Composable
private fun CreateButton(
    testTag: String,
    activityLauncher: (context: Context) -> Unit,
    text: String
) {
    val context = LocalContext.current
    MainMenuButton(
        testTag = testTag,
        onClick = { activityLauncher(context) },
        text = text
    )
}

/**
 * Screen of the Wordle menu application.
 */
@Composable
fun WordleMenuScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(WordleMenu.WORDLE_MENU_TEST_TAG),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreateButton(
            WordleMenu.Companion.Difficulty.EASY.prettyText,
            { a: Context -> launchGame(a, WordleMenu.Companion.Difficulty.EASY.name) },
            WordleMenu.Companion.Difficulty.EASY.prettyText + lettersOrWordOnly(WordleMenu.Companion.Difficulty.EASY)
        )
        CreateButton(
            WordleMenu.Companion.Difficulty.MEDIUM.prettyText,
            { a: Context -> launchGame(a, WordleMenu.Companion.Difficulty.MEDIUM.name) },
            WordleMenu.Companion.Difficulty.MEDIUM.prettyText + lettersOrWordOnly(WordleMenu.Companion.Difficulty.MEDIUM)
        )
        CreateButton(
            WordleMenu.Companion.Difficulty.HARD.prettyText,
            { a: Context -> launchGame(a, WordleMenu.Companion.Difficulty.HARD.name) },
            WordleMenu.Companion.Difficulty.HARD.prettyText + lettersOrWordOnly(WordleMenu.Companion.Difficulty.HARD)
        )
        CreateButton(
            WordleMenu.Companion.Difficulty.VERY_HARD.prettyText,
            { a: Context -> launchGame(a, WordleMenu.Companion.Difficulty.VERY_HARD.name) },
            WordleMenu.Companion.Difficulty.VERY_HARD.prettyText + lettersOrWordOnly(WordleMenu.Companion.Difficulty.VERY_HARD)
        )
        CreateButton(
            WordleMenu.Companion.Difficulty.VERY_VERY_HARD.prettyText,
            { a: Context -> launchGame(a, WordleMenu.Companion.Difficulty.VERY_VERY_HARD.name) },
            WordleMenu.Companion.Difficulty.VERY_VERY_HARD.prettyText + lettersOrWordOnly(WordleMenu.Companion.Difficulty.VERY_VERY_HARD)
        )
    }
}

private fun lettersOrWordOnly(difficulty: WordleMenu.Companion.Difficulty): String {
    if (WordleGameActivity.Companion.difficultyIsWordOnly.getOrDefault(difficulty, false)) {
        return WordleMenu.WORD_ONLY
    }
    return WordleMenu.LETTERS
}