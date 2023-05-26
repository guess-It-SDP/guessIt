package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.MainMenuActivity.Companion.CHAT
import com.github.freeman.bootcamp.MainMenuActivity.Companion.PLAY
import com.github.freeman.bootcamp.MainMenuActivity.Companion.RECAPS
import com.github.freeman.bootcamp.MainMenuActivity.Companion.SETTINGS
import com.github.freeman.bootcamp.games.guessit.DisplayRecapsActivity
import com.github.freeman.bootcamp.games.guessit.chat.ChatActivity
import com.github.freeman.bootcamp.games.guessit.drawing.DrawingActivity
import com.github.freeman.bootcamp.games.guessit.guessing.GuessingActivity
import com.github.freeman.bootcamp.games.wordle.WordleMenu
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.createProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * This class will be the main screen of the app
 */
class MainMenuActivity : ComponentActivity() {
    private val backgroundMusicService = BackgroundMusicService()
    private var userId = Firebase.auth.uid
    private val dbRef = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = Intent(this, backgroundMusicService::class.java)
        startService(i)
        setContent {
            val context = LocalContext.current

            // Checks if it is the first time launching the app by looking if a profile exists.
            // If no profile exists, sign in anonymously and creates a profile
            dbRef
                .child(getString(R.string.profiles_path))
                .child(userId.toString())
                .child(getString(R.string.username_path))
                .get()
                .addOnCompleteListener {

                // If no profile exists
                if (it.result.value == "" || it.result.value == null) {
                    // sign in anonymously
                    Firebase.auth.signInAnonymously().addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            userId = Firebase.auth.uid.toString()
                            createProfile(context, userId!!, "Guest")
                        }
                    }
                }
            }

            BootcampComposeTheme {
                MainMenuScreen()
            }
        }
    }

    companion object {
        const val SETTINGS = "Settings"
        const val PLAY = "Play Game"
        const val CHAT = "Chat"
        const val RECAPS = "Recaps"
    }
}

@Composable
fun MainMenuButton(testTag: String, onClick: () -> Unit, text: String, icon: ImageVector = Icons.Default.Add) {
    ElevatedButton(
        modifier= Modifier
            .testTag(testTag)
            .padding(16.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors()
    ) {
        Row (
            modifier = Modifier
                .padding(5.dp)
        ){
            Icon(
                imageVector = icon,
                contentDescription = "menu button",
                modifier = Modifier.size(23.dp)
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(text)
        }

    }

}

fun play(context: Context) {
    context.startActivity(Intent(context, GamesMenuActivity::class.java))
}

@Composable
fun PlayButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "playButton",
        onClick = { play(context) },
        text = PLAY,
        icon = Icons.Filled.PlayArrow
    )
}

@Composable
fun DisplayRecapsButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "displayRecapsButton",
        onClick = { context.startActivity(Intent(context, DisplayRecapsActivity::class.java)) },
        text = RECAPS,
        icon = Icons.Filled.List
    )
}

fun settings(context: Context) {
    context.startActivity(Intent(context, SettingsProfileActivity::class.java))
}

@Composable
fun SettingsButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "settingsButton",
        onClick = { settings(context) },
        text = SETTINGS,
        icon = Icons.Filled.Settings
    )
}


fun chatTest(context: Context) {
    context.startActivity(Intent(context, ChatActivity::class.java).apply {
        putExtra(context.getString(R.string.gameId_extra), context.getString(R.string.test_game_id))
    })
}

@Composable
fun ChatButton() {
    val context = LocalContext.current
    MainMenuButton(
        testTag = "chatButton",
        onClick = { chatTest(context) },
        text = CHAT,
        icon = Icons.Filled.Email
    )
}

fun guessing(context: Context, gameId: String) {
    context.startActivity(Intent(context, GuessingActivity::class.java).apply {
        putExtra(context.getString(R.string.gameId_extra), gameId)
    })
}

fun drawing(context: Context, gameId: String) {
    context.startActivity(Intent(context, DrawingActivity::class.java).apply {
        putExtra(context.getString(R.string.gameId_extra), gameId)
    })
}

fun wordle(context: Context) {
    context.startActivity(Intent(context, WordleMenu::class.java))
}

@Composable
fun AppTitle() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gameName"),
                text = context.getString(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = context.getString(R.string.app_catch_phrase),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MainMenuScreen() {

    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val options = BitmapFactory.Options().apply {
                inSampleSize = 4 // Reduce the image size by half
            }
            val bitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.backgroundlight, options)

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "My Background Image",
                contentScale = ContentScale.FillBounds,
                colorFilter = tint(MaterialTheme.colorScheme.primaryContainer)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("mainMenuScreen"),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                AppTitle()

                Spacer(Modifier.size(50.dp))

                PlayButton()
                Row {
                    SettingsButton()
                    ChatButton()
                }
                DisplayRecapsButton()

            }
        }
    }
}

@Preview
@Composable
fun AppTitlePreview() {
    BootcampComposeTheme {
        AppTitle()
    }
}
