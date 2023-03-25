package com.github.freeman.bootcamp.wordle

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
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.material.Button


class WordleGameActivity : ComponentActivity() {
    lateinit var wordle: WordleGameState
    lateinit var solutionsData: String
    lateinit var validWordsData: String
    lateinit var solutions: List<String>
    lateinit var validWords: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        validWordsData = application.assets.open("wordle_all.txt").bufferedReader().use {
            it.readText()
        }
        solutionsData = application.assets.open("wordle_all.txt").bufferedReader().use {
            it.readText()
        }
        solutions = solutionsData.split("\n").map { it.trim() }
        validWords = validWordsData.split("\n").map { it.trim() }
        wordle = WordleGameState(false, solutions, validWords)
        var tiles = getTiles(wordle.getGrid())
        setContent {
            Column() {
                BootcampComposeTheme {
                    TileRoof(
                        tiles
                    )
                       SimpleText()
                }
            }
        }
    }

    @Composable
    fun SimpleText() {
        Text("Hello")
        Button(onClick = {}) {
            wordle = wordle.submitWord("")
            var tiles = getTiles(wordle.getGrid())
            setContent {
                BootcampComposeTheme {
                    TileRoof(
                        tiles
                    )
                }
            }
        }
    }

    private fun getTiles(grid: Array<Array<WordleGameState.Tile>>): MutableList<WordleGameState.Tile> {
        val tiles: MutableList<WordleGameState.Tile> = ArrayList()
        grid.forEach { row -> tiles.addAll(row.toList()) }
        return tiles
    }

    @Composable
    fun TileRoof(tiles: MutableList<WordleGameState.Tile>) {
        var id = 0
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(50.dp)
        ) {
            items(tiles.size) { i ->
                TileContainer(Modifier, tile = tiles.get(i))
            }
        }
    }

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
                    color = Color(tile.state.rgb),
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
