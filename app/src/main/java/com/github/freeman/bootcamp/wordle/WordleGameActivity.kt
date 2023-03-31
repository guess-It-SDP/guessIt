package com.github.freeman.bootcamp.wordle

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.GameOptionsActivity
import com.github.freeman.bootcamp.Main
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.touchlane.gridpad.GridPad
import com.touchlane.gridpad.GridPadCells
import androidx.compose.runtime.remember as remember1
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


import androidx.compose.ui.text.input.TextFieldValue

/**
 * The is the class that allow the play to the wordle game.
 * This is and adapter from WordleGameState to android
 */
class WordleGameActivity() : ComponentActivity() {
    private lateinit var wordle: WordleGameState
    private lateinit var solutionsData: String
    private lateinit var validWordsData: String
    lateinit var solutions: List<String>
    lateinit var validWords: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        validWordsData = application.assets.open("wordle_all.txt").bufferedReader().use {
            it.readText()
        }
        solutionsData = application.assets.open("wordle_common.txt").bufferedReader().use {
            it.readText()
        }
        solutions = solutionsData.split("\n").map { it.trim() }
        validWords = validWordsData.split("\n").map { it.trim() }
        wordle = WordleGameState(false, solutions, validWords)
        val tiles = wordle.getTiles()
        val testing = getIntent().getBooleanExtra("testing", false)
        if (testing == true) {
            //#### for testing #test
            wordle = wordle.withSetWordToGuess("hello")
        }
        setContent {
            Column() {
                BootcampComposeTheme {
                    TileRoof(
                        tiles
                    )
                    wordleButton()
                }
            }
        }
    }

    /**
     * submit a word to the game and reset the graphic interface
     */
    @Composable
    private fun wordleButton() {
        val msg: TextFieldState = remember { TextFieldState() }
        GreetingInput(msg)
        Button(onClick = {
            if (msg.text.length == 5) {
                wordle = wordle.withSubmittedWord(
                    msg.text
                )
                var tiles = wordle.getTiles()
                setContent {
                    Column() {
                        BootcampComposeTheme {
                            TileRoof(
                                tiles
                            )
                            wordleButton()
                        }
                    }
                }
            }
        }) { Text(text = "Submit word", color = Color.Magenta) }
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
                    Modifier.testTag("wordle_tile_id_" + id.toString()),
                    tile = tiles.get(i)
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
}

// this class is to store the value of the text field
// in order to use it in other Composable
private class TextFieldState {
    var text: String by mutableStateOf("")
}

/**
 * text-field where the player enters a word to try to guess the hidden wordToGuess
 * @param location to save the input
 */
@Composable
private fun GreetingInput(msg: TextFieldState = remember { TextFieldState() }) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    OutlinedTextField(
        value = text,
        label = {
            Text(text = "Enter a 5 letters word to submit" )
        },
        onValueChange = {
            text = it
            msg.text = it.text
        }
    )
}

