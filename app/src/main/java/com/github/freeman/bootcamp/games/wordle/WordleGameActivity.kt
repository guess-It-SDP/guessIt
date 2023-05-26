package com.github.freeman.bootcamp.games.wordle


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.games.wordle.WordleGameActivity.Companion.WORDLE_GAME_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import androidx.compose.runtime.remember as remember1

/**
 * The is the class that allow the play to the wordle game.
 * This is and adapter from WordleGameState to android
 */
class WordleGameActivity : ComponentActivity() {
    private lateinit var wordle: WordleGameState
    private lateinit var solutionsData: String // a text-field containing the possibles word to guess
    private lateinit var validWordsData: String // a text-field containing word that the player is allowed to submit in wordOnly
    private lateinit var easysWordsData: String // a text-field containing easy words
    private lateinit var solutions: List<String>
    private lateinit var validWords: List<String>
    private lateinit var easyWords: List<String>
    private lateinit var int: Intent

    companion object {
        private const val VALID_WORDS_FILE = "wordle_all.txt"
        private const val SOLUTIONS_FILE = "wordle_common.txt"
        private const val EASY_WORDS_FILE = "wordle_easy.txt"

        const val SUBMISSION_TEXTFIELD = "Enter a 5 letters word to submit"

        private const val NB_ROW_EASY = 9
        private const val NB_ROW_MEDIUM = 8
        private const val NB_ROW_HARD = 6
        private const val NOT_5_LETTERS_PLEASE = "You need to enter 5 letters."
        private const val NOT_WRONG_WORDS_PLEASE = "Please enter a valid word."

        const val WORDLE_GAME_TITLE = "Wordle"

        val difficultyIsWordOnly = mapOf(
            WordleMenu.Companion.Difficulty.EASY to false,
            WordleMenu.Companion.Difficulty.MEDIUM to false,
            WordleMenu.Companion.Difficulty.HARD to true,
            WordleMenu.Companion.Difficulty.VERY_HARD to true,
            WordleMenu.Companion.Difficulty.VERY_VERY_HARD to true
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        int = intent

        // load the data from the text files
        validWordsData = application.assets.open(VALID_WORDS_FILE).bufferedReader().use {
            it.readText()
        }
        solutionsData = application.assets.open(SOLUTIONS_FILE).bufferedReader().use {
            it.readText()
        }
        easysWordsData = application.assets.open(EASY_WORDS_FILE).bufferedReader().use {
            it.readText()
        }
        solutions = solutionsData.split("\n").map { it.trim() }
        validWords = validWordsData.split("\n").map { it.trim() }
        easyWords = validWordsData.split("\n").map { it.trim() }


        setUpGame()

        val tiles = wordle.getTiles()

        setContent {
            WordleGameScreen(tiles)
        }
    }

    /**
     * set up the game according to difficulty level chosen
     */
    private fun setUpGame() {
        wordle = when (int.getStringExtra(WordleMenu.Companion.Difficulty::class.simpleName)) {
            WordleMenu.Companion.Difficulty.EASY.name -> WordleGameState.startGame(difficultyIsWordOnly.getOrDefault(WordleMenu.Companion.Difficulty.EASY,false), easyWords, validWords, NB_ROW_EASY)
            WordleMenu.Companion.Difficulty.MEDIUM.name -> WordleGameState.startGame(difficultyIsWordOnly.getOrDefault(WordleMenu.Companion.Difficulty.MEDIUM,false), easyWords, validWords, NB_ROW_MEDIUM)
            WordleMenu.Companion.Difficulty.HARD.name -> WordleGameState.startGame(difficultyIsWordOnly.getOrDefault(WordleMenu.Companion.Difficulty.HARD,false), easyWords, validWords, NB_ROW_MEDIUM)
            WordleMenu.Companion.Difficulty.VERY_HARD.name -> WordleGameState.startGame(difficultyIsWordOnly.getOrDefault(WordleMenu.Companion.Difficulty.VERY_HARD,false), solutions, validWords, NB_ROW_MEDIUM)
            WordleMenu.Companion.Difficulty.VERY_VERY_HARD.name -> WordleGameState.startGame(difficultyIsWordOnly.getOrDefault(WordleMenu.Companion.Difficulty.VERY_VERY_HARD,false), solutions, validWords, NB_ROW_HARD)
            else -> WordleGameState.startGame(false, solutions, easyWords, NB_ROW_EASY)
        }
    }

    /**
     * Submit a word, used in WordleButton()
     * @return true if the entry text is valid, false otherwise
     */
    private fun submit(text: String): Boolean {
        val difficulty =
            int.getStringExtra(WordleMenu.Companion.Difficulty::class.simpleName)
        if (text.length == 5) {
            if (
                difficulty == WordleMenu.Companion.Difficulty.HARD.name
                || difficulty == WordleMenu.Companion.Difficulty.VERY_HARD.name
                || difficulty == WordleMenu.Companion.Difficulty.VERY_VERY_HARD.name
            ) {
                if (!validWords.contains(text)) {
                    Toast.makeText(
                        applicationContext,
                        NOT_WRONG_WORDS_PLEASE,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            wordle = wordle.withSubmittedWord(
                text
            )
            val tiles = wordle.getTiles()
            setContent {
                WordleGameScreen(tiles)
            }
            return true
        } else {
            Toast.makeText(applicationContext, NOT_5_LETTERS_PLEASE, Toast.LENGTH_LONG).show()
            return false
        }
    }

    /**
     * submit a word to the game and reset the graphic interface
     */
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun WordleButton() {
        val msg: TextFieldState = remember1 { TextFieldState() }
        val keyboardController = LocalSoftwareKeyboardController.current

        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            GreetingInput(msg)
            IconButton(
                modifier = Modifier.testTag("submitWordButton"),
                onClick = {
                    val tx = msg.text.lowercase().replace("\\s".toRegex(), "")
                    if (submit(tx)) {
                        keyboardController?.hide()
                        msg.text = ""
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Submit word",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }


    }

    /**
     * This is the grid containing all the tiles
     */
    @Composable
    private fun TileRoof(tiles: MutableList<WordleGameState.Tile>) {
        var id = 0

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(50.dp)
                .testTag("wordle_tile_grid")
        ) {
            items(tiles.size) { i ->

                TileContainer(
                    Modifier.testTag("wordle_tile_id_$id"),
                    tile = tiles[i]
                )
                ++id
            }
        }
    }

    /**
     * hold a letter and the color correspond to the state of the letter
     * at the right position,at the wrong sport or the the wordToguess does not contains the letter
     */
    @Composable
    private fun TileContainer(
        modifier: Modifier,
        tile: WordleGameState.Tile
    ) {
        val shape = remember1 { RoundedCornerShape(4.dp) }
        Box(
            modifier = modifier
                .size(
                    width = 29.dp,
                    height = 40.dp,
                )
                .background(
                    color = Color(tile.state.argb),
                    shape = shape,
                )
                .run {

                    this

                },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = tile.letter.toString())
        }
    }

    /**
     * text-field where the player enters a word to try to guess the hidden wordToGuess
     * @param msg to save the input
     */
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun GreetingInput(msg: TextFieldState = remember1 { TextFieldState() }) {
        var text by remember1 { mutableStateOf(TextFieldValue("")) }

        val keyboardController = LocalSoftwareKeyboardController.current

        OutlinedTextField(
            value = text,
            label = {
                Text(text = SUBMISSION_TEXTFIELD)
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    val tx = msg.text.lowercase().replace("\\s".toRegex(), "")
                    if (submit(tx)) {
                        keyboardController?.hide()
                        msg.text = ""
                    }
                }
            ),
            //onImeActionPerformed = {},
            onValueChange = {
                text = it
                msg.text = it.text
            },
            singleLine = true
        )
    }

    @Composable
    private fun WordleGameScreen(tiles: MutableList<WordleGameState.Tile>) {
        BootcampComposeTheme {
            Surface {
                Column (
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopAppbarWordleGame()
                    Box (
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    ) {
                        TileRoof(tiles)
                    }
                    WordleButton()
                }
            }
        }
    }
}

// this class is to store the value of the text field
// in order to use it in other Composable
private class TextFieldState {
    var text: String by mutableStateOf("")
}

@Composable
fun TopAppbarWordleGame() {
    val context = LocalContext.current

    TopAppBar(
        modifier = Modifier.testTag("topAppbarWordleGame"),
        title = {
            Text(
                text = WORDLE_GAME_TITLE,
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
            androidx.compose.material3.IconButton(
                modifier = Modifier
                    .testTag("appBarBack"),
                onClick = {
                    val activity = (context as? Activity)
                    activity?.finish()
                },
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    )
}



