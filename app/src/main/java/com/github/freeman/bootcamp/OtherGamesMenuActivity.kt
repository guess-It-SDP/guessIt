package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.wordle.WordleGameActivity

class OtherGamesMenuActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                GameMenuScreen()
            }
        }
    }
}


fun worlde(context: Context) {
    context.startActivity(Intent(context, WordleGameActivity::class.java))

}

@Composable
fun worldeButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("wordle_button"),
        onClick = { worlde(context) }
    ) {
        Text("Play Wordle")
    }
}

/**
 * This class will be the main screen of the app
 * This class is not debugging
 */
@Composable
fun GameMenuScreen() {
    Box(
        modifier = with (Modifier){
            fillMaxSize()
                .paint(
                    // Replace with your image id
                    painterResource(id = R.drawable.dinos),
                    contentScale = ContentScale.FillBounds)

        }) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.testTag("other_games_text"),
                text = "OtherGames",
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.size(50.dp))
            worldeButton()
        }
    }
}

