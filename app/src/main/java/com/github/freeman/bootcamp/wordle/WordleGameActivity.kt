package com.github.freeman.bootcamp.wordle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import com.github.freeman.bootcamp.Main
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import androidx.compose.runtime.remember as remember1

class WordleGameActivity : ComponentActivity() {
    lateinit var wordle: WordleGameState
    lateinit var list: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wordle = WordleGameState(false)
        setContent {
            BootcampComposeTheme {
                TileRoof(
                    wordle.getGrid()
                )
            }
        }
    }
}

@Composable
fun TileRoof(grid: Array<Array<WordleGameState.Tile>>) {
    var id = 0
    Column {
        grid.forEach { row ->
            row.forEach { tile ->
                TileContainer(modifier = Modifier.testTag(id.toString()), tile = tile)
                ++id
            }
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

