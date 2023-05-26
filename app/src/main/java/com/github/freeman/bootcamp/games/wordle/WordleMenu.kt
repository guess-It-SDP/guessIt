package com.github.freeman.bootcamp.games.wordle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.MainMenuButton
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity.Companion.difficultyIsWordOnly
import com.github.freeman.bootcamp.games.wordle.WordleMenu.Companion.Difficulty
import com.github.freeman.bootcamp.games.wordle.WordleMenu.Companion.LETTERS
import com.github.freeman.bootcamp.games.wordle.WordleMenu.Companion.WORDLE_MENU_TEXT
import com.github.freeman.bootcamp.games.wordle.WordleMenu.Companion.WORDLE_MENU_TITLE
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

        const val WORDLE_MENU_TITLE = "Wordle"
        const val WORDLE_MENU_TEXT = "Choose a difficulty level:"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                Surface {
                    TopAppbarWordleMenu()
                    WordleMenuScreen()
                }
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
 * Creates a button to launch a game
 *
 *@param difficulty The difficulty of the game that will be launched
 */
@Composable
private fun DifficultyButton(
    difficulty: Difficulty,
    icon: ImageVector
) {
    val context = LocalContext.current
    MainMenuButton(
        testTag = difficulty.prettyText,
        onClick = { launchGame(context, difficulty.name) } ,
        text = difficulty.prettyText + lettersOrWordOnly(difficulty),
        icon = icon
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

        Text(
            modifier = Modifier
                .testTag("wordleMenuText")
                .align(Alignment.CenterHorizontally),
            text = WORDLE_MENU_TEXT,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.size(20.dp))

        DifficultyButton(Difficulty.EASY, ImageVector.vectorResource(R.drawable.level1))
        DifficultyButton(Difficulty.MEDIUM, ImageVector.vectorResource(R.drawable.level2))
        DifficultyButton(Difficulty.HARD, ImageVector.vectorResource(R.drawable.level3))
        DifficultyButton(Difficulty.VERY_HARD, ImageVector.vectorResource(R.drawable.level4))
        DifficultyButton(Difficulty.VERY_VERY_HARD, ImageVector.vectorResource(R.drawable.level5))
    }
}

private fun lettersOrWordOnly(difficulty: Difficulty): String {
    if (difficultyIsWordOnly.getOrDefault(difficulty, false)) {
        return WORD_ONLY
    }
    return LETTERS
}

@Composable
fun TopAppbarWordleMenu() {
    val context = LocalContext.current

    TopAppBar(
        modifier = Modifier.testTag("topAppbarWordleMenu"),
        title = {
            Text(
                modifier = Modifier.testTag("topAppbarWordleMenuTitle"),
                text = WORDLE_MENU_TITLE,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 20.sp
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(
                modifier = Modifier
                    .testTag("appBarBack"),
                onClick = {
                    val activity = (context as? Activity)
                    activity?.finish()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    )
}