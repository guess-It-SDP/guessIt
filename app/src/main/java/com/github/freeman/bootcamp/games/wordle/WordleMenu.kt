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
import com.github.freeman.bootcamp.MainMenuButton
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity.Companion.difficultyIsWordOnly
import com.github.freeman.bootcamp.games.wordle.WordleMenu.Companion.GAME_RULES
import com.github.freeman.bootcamp.games.wordle.WordleMenu.Companion.Difficulty
import com.github.freeman.bootcamp.games.wordle.WordleMenu.Companion.LETTERS
import com.github.freeman.bootcamp.games.wordle.WordleMenu.Companion.WORD_ONLY
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme


/**
 * The is the class that allow the play to the wordle game.
 * This is and adapter from WordleGameState to android
 */
class WordleMenu : ComponentActivity() {
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
 * Change the activity to the game activity and put the difficulty strong in the extras
 */
private fun launch(context: Context) {
    context.startActivity(Intent(context, WordleRulesActivity::class.java))
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
            GAME_RULES,
            ::launch,
            GAME_RULES
        )
        CreateButton(
            Difficulty.EASY.prettyText,
            { a: Context -> launchGame(a, Difficulty.EASY.name) },
            Difficulty.EASY.prettyText + lettersOrWordOnly(Difficulty.EASY)
        )
        CreateButton(
            Difficulty.MEDIUM.prettyText,
            { a: Context -> launchGame(a, Difficulty.MEDIUM.name) },
            Difficulty.MEDIUM.prettyText + lettersOrWordOnly(Difficulty.MEDIUM)
        )
        CreateButton(
            Difficulty.HARD.prettyText,
            { a: Context -> launchGame(a, Difficulty.HARD.name) },
            Difficulty.HARD.prettyText + lettersOrWordOnly(Difficulty.HARD)
        )
        CreateButton(
            Difficulty.VERY_HARD.prettyText,
            { a: Context -> launchGame(a, Difficulty.VERY_HARD.name) },
            Difficulty.VERY_HARD.prettyText + lettersOrWordOnly(Difficulty.VERY_HARD)
        )
        CreateButton(
            Difficulty.VERY_VERY_HARD.prettyText,
            { a: Context -> launchGame(a, Difficulty.VERY_VERY_HARD.name) },
            Difficulty.VERY_VERY_HARD.prettyText + lettersOrWordOnly(Difficulty.VERY_VERY_HARD)
        )
    }
}

private fun lettersOrWordOnly(difficulty: Difficulty): String {
    if (difficultyIsWordOnly.getOrDefault(difficulty, false)) {
        return WORD_ONLY
    }
    return LETTERS
}