package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.CATEGORIES_SELECTION
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.DEFAULT_CATEGORY_SIZE
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.NB_ROUNDS
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.NEXT
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.ROUNDS_SELECTION
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.TOAST_TEXT
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.categories
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.categorySize
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.selectedCategory
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.selectedTopics
import com.github.freeman.bootcamp.games.guessit.GameOptionsActivity.Companion.selection
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * Here the player can chose different settings for the game he wants to create.
 */
class GameOptionsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbRef = Firebase.database.reference
        setContent {
            BootcampComposeTheme {
                GameOptionsScreen(dbRef)
            }
        }
    }

    companion object {
        const val CATEGORIES_SELECTION = "Select a category"
        const val ROUNDS_SELECTION = "Select the number of rounds"
        const val NEXT = "Next"
        const val NB_TOPICS = 3
        const val DEFAULT_CATEGORY_SIZE = 0
        const val TOAST_TEXT = "Please first select a category"

        val categories = listOf("Animals", "People", "Objects")
        var selectedCategory = categories[0]
        val NB_ROUNDS = listOf("1", "3", "5", "7", "9")
        var selection: Int = 5
        var selectedTopics = mutableListOf<String?>()
        var categorySize = DEFAULT_CATEGORY_SIZE
    }
}

/**
 * Create the radio buttons in order to chose a different number of rounds to be played during
 * the Game.
 */
@Composable
fun RoundsDisplay() {
    val kinds = NB_ROUNDS
    val (selected, setSelected) = remember { mutableStateOf("5") }
    RoundsRadioButtons(mItems = kinds, selected, setSelected)
    selection = Integer.parseInt(selected)
}


/**
 * Create the radio buttons in order to chose a different number of rounds to be played during the
 * Game.
 *
 * @param The list of possible number of rounds a player can chose
 * @param selected corresponds to the value inside the MutableState object currently chosen by the
 * player(has a default value)
 * @param SetSelected corresponds to the function that can be used to update the value inside the
 * MutableState object.
 */
@Composable
fun RoundsRadioButtons(mItems: List<String>, selected: String, setSelected: (selected: String) -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            mItems.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected == item,
                        onClick = {
                            setSelected(item)
                        },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Blue
                        )
                    )
                    Text(
                        text = item,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .testTag("radioButtonText$item"),
                    )
                }
            }
        }
    }
}

/**
 * Randomly chose topics that Will be presented to the player. Create the RadioButtons so the player can
 * chose which topics he wants to use in his next Game.
 *
 */
@Composable
fun CategoriesDisplay() {
    val (selectedIndex, setSelected) = remember { mutableStateOf(-1) }
    val (size, setSize) = remember { mutableStateOf(DEFAULT_CATEGORY_SIZE) }
    val (topics, setTopics) = remember { mutableStateOf(arrayOf<String>()) }
    CategoriesRadioButtons(selectedIndex, setSelected, setSize, setTopics)

    categorySize = size

    if (topics.isNotEmpty() && categorySize > 0) {
        selectedTopics.clear()
        val allTopics = topics.toMutableList()
        val selectedIndices = allTopics.indices.shuffled().take(categorySize)
        selectedTopics.addAll(selectedIndices.map { allTopics[it] })
    }
}

/**
 * Create the RadioButtons so the player can
 * chose which topics he wants to use in his next Game.
 */
@Composable
fun CategoriesRadioButtons(selectedIndex: Int, setSelected: (selected: Int) -> Unit,
                           setSize: (topics: Int) -> Unit, setTopics: (topics: Array<String>) -> Unit) {

    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        val cornerRadius = 16.dp

        categories.forEachIndexed { index, item ->
            OutlinedButton(
                onClick = {
                    selectedCategory = categories[index]
                    setSelected(index)
                    fetchFromDB(context, setSize, setTopics)
                },
                shape = when (index) {
                    0 -> RoundedCornerShape(
                        topStart = cornerRadius,
                        topEnd = 0.dp,
                        bottomStart = cornerRadius,
                        bottomEnd = 0.dp
                    )
                    categories.size - 1 -> RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = cornerRadius,
                        bottomStart = 0.dp,
                        bottomEnd = cornerRadius
                    )
                    else -> RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                },
                border = BorderStroke(
                    1.dp, if (selectedIndex == index) {
                        Color.Blue
                    } else {
                        Color.Blue.copy(alpha = 0.75f)
                    }
                ),
                colors = if (selectedIndex == index) {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Blue.copy(alpha = 0.15f),
                        contentColor = Color.Blue
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Blue.copy(alpha = 0.02f),
                        contentColor = Color.Blue
                    )
                }
            ) {
                Text(
                    text = item,
                    modifier = Modifier.testTag("categoryButtonText$item"))
            }
        }
    }

}

@Composable
fun NextButton(dbRef: DatabaseReference) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("nextButton"),
        onClick = {
            next(context, dbRef)
        }
    ) {
        Text(NEXT)
    }
}

/**
 * This function is used to start a new game by creating a new game in the Firebase Realtime
 * Database. First, it gets the current user's ID from Firebase authentication.
 * If the user is not authenticated, the ID is set to "null". Then, it creates a new reference
 * to the "games/" node in the database and generates a unique key for the new game.
 * If categorySize is less than or equal to zero, a Toast is displayed to prompt the user to
 * select a category. Otherwise, it retrieves the user's username from their profile
 * in the database. After retrieving the user's username, it creates a new GameData object,
 * which contains information about the game. After creating the GameData object,
 * it sets the new game's data in the database using the setValue function.
 * Then, it starts a new WaitingRoomActivity and passes in the new game's ID and
 * selected topics as extras. Finally, it finishes the current activity.
 *
 *@param context Information about application environnement.
 *@param database A particular location inside the database.
 */
fun next(context: Context, database: DatabaseReference) {
    var userId = Firebase.auth.uid
    userId = userId ?: "null"
    val dbref = database.child(context.getString(R.string.games_path))
    val gameId = dbref.push().key

    if (categorySize <= 0) {
        Toast.makeText(context, TOAST_TEXT, Toast.LENGTH_SHORT).show()
    } else {

        val dbrefUsername = database
            .child(context.getString(R.string.profiles_path))
            .child(userId)
            .child(context.getString(R.string.username_path))
        FirebaseUtilities.databaseGet(dbrefUsername)
            .thenAccept {

                val gameData = GameData(
                    Current = Current(
                        correct_guesses = 0,
                        current_artist = userId,
                        current_round = 0,
                        current_state = context.getString(R.string.state_waitingforplayers),
                        current_turn = 0,
                        current_timer = context.getString(R.string.timer_unused)
                    ),
                    Parameters = Parameters(
                        category = selectedCategory,
                        host_id = userId,
                        nb_players = 1,
                        nb_rounds = selection
                    ),
                    Players = mapOf(Pair(userId, Player(0, false))),
                    lobby_name = "$it's room"
                )

                dbref.child(gameId!!).setValue(gameData)

                context.startActivity(Intent(context, WaitingRoomActivity::class.java).apply {
                    putExtra(context.getString(R.string.gameId_extra), gameId)
                    for (i in 0 until selectedTopics.size) {
                        putExtra("topic$i", selectedTopics[i])
                    }
                })
                val activity = (context as? Activity)
                activity?.finish()
            }

    }
}

fun fetchFromDB(context: Context, setSize: (topics: Int) -> Unit, setTopics: (topics: Array<String>) -> Unit) {
    val dbrefTopics = Firebase.database.reference
        .child(context.getString(R.string.topics_path))
        .child(selectedCategory)

    // Fetch the number of topics present in the given category
    dbrefTopics.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val fetchedSize = dataSnapshot.childrenCount.toInt() - 1
            setSize(fetchedSize)
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    })

    fetchTopics(context, setTopics)
}

fun fetchTopics(context: Context, setTopics: (topics: Array<String>) -> Unit) {
    val dbrefTopics = Firebase.database.reference
        .child(context.getString(R.string.topics_path))
        .child(selectedCategory)

    // Fetches topics from the database
    dbrefTopics.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val fetchedTopics = snapshot.getValue<ArrayList<String>>()!!
                setTopics(fetchedTopics.toTypedArray())
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    })
}

@Composable
fun GameOptionsBackButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("gameOptionsBackButton"),
        onClick = {
            backToMainMenu(context)
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back arrow icon"
        )
    }
}

/**
 * It first checks if the context passed to the function is an instance of an Activity.
 * If it is, it calls the finish() method on the activity, which finishes the current
 * activity and returns the user to the previous one in the activity stack (in this case, the main menu activity).
 * If it's not, nothing happens.
 */
fun backToMainMenu(context: Context) {
    val activity = (context as? Activity)
    activity?.finish()
}

/**
 * Display the screen containing the differents parameters the player can chose from to create
 * a new game.
 * @param dbRef a particular location in the database
 */
@Composable
fun GameOptionsScreen(dbRef: DatabaseReference) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("gameOptionsScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.testTag("categoriesSelection"),
            text = CATEGORIES_SELECTION
        )
        Spacer(modifier = Modifier.size(10.dp))
        CategoriesDisplay()
        Spacer(modifier = Modifier.size(50.dp))
        Text(
            modifier = Modifier.testTag("roundsSelection"),
            text = ROUNDS_SELECTION
        )
        RoundsDisplay()
        NextButton(dbRef)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        GameOptionsBackButton()
    }
}
