package com.github.freeman.bootcamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.ScoreActivity.Companion.size
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class ScoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                ScoreScreen(listOf(Pair("Boris", 10), Pair("Alistair", 3), Pair("Craig", 7)))
            }
        }
    }

    companion object {
        const val size = 200
    }
}

@Composable
fun ScoreScreen(playerScores: List<Pair<String, Int>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(size.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Scoreboard(
            playerScores = playerScores,
            modifier = Modifier.width((0.475 * size).dp).height((0.575 * size).dp)
        )
    }
}

@Composable
fun Scoreboard(playerScores: List<Pair<String, Int>>, modifier: Modifier) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = modifier
            .padding(2.dp)
            .fillMaxHeight()
            .background(Color.LightGray, RoundedCornerShape(10.dp))
            .onSizeChanged { size = it }
    ) {
        Column(
            modifier = Modifier.padding(6.dp).fillMaxSize()
        ) {
            Text(
                text = "Scores",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(2.dp))
            playerScores.forEach { (name, score) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = score.toString(),
                        style = MaterialTheme.typography.body2,
                    )
                }
                Divider(color = Color.Black, thickness = 1.dp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScorePreview() {
    ScoreScreen(listOf(Pair("Boris", 10), Pair("Alistair", 3), Pair("Craig", 7)))
}
