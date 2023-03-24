package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.CATEGORIES_SELECTION
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.DEFAULT_CATEGORY_SIZE
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NB_ROUNDS
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NEXT
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.ROUNDS_SELECTION
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.categories
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.category_size
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.selectedCategory
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.selectedTopics
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.selection
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*


class GameOptionsActivity : ComponentActivity() {

    private val gameId = UUID.randomUUID().toString()
    private val dbref = Firebase.database.getReference("Games/$gameId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                GameOptionsScreen(dbref, gameId)
            }
        }
    }

    companion object {
        const val CATEGORIES_SELECTION = "Select a category"
        const val ROUNDS_SELECTION = "Select the number of rounds"
        const val NEXT = "Next"
        const val NB_TOPICS = 3
        const val DEFAULT_CATEGORY_SIZE = 0
        val categories = listOf("Animals", "Test")
        var selectedCategory = categories[0]
        val NB_ROUNDS = listOf("1", "3", "5", "7", "9")
        var selection: Int = 5
        var selectedTopics = mutableListOf<String?>()
        var category_size = DEFAULT_CATEGORY_SIZE
    }
}

@Composable
fun RoundsDisplay() {
    val kinds = NB_ROUNDS
    val (selected, setSelected) = remember { mutableStateOf("5") }
    RoundsRadioButtons(mItems = kinds, selected, setSelected)
    selection = Integer.parseInt(selected)
}

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

@Composable
fun CategoriesDisplay() {
    val (selectedIndex, setSelected) = remember { mutableStateOf(0) }
    CategoriesRadioButtons(selectedIndex, setSelected)
    selectedCategory = categories[selectedIndex]
//    Toast.makeText(LocalContext.current, "selectedCategory: $selectedCategory", Toast.LENGTH_SHORT).show()
}

@Composable
fun CategoriesRadioButtons(selectedIndex: Int, setSelected: (selected: Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        val cornerRadius = 16.dp

        categories.forEachIndexed { index, item ->
            OutlinedButton(
                onClick = { setSelected(index) },
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
                        containerColor = Color.Blue.copy(alpha = 0.01f),
                        contentColor = Color.Blue
                    )
                }
            ) {
                Text(item)
            }
        }
    }
}

@Composable
fun NextButton(dbref: DatabaseReference, gameId: String) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("nextButton"),
        onClick = {
            next(context, dbref, gameId)
        }
    ) {
        Text(NEXT)
    }
}


@Composable
fun FetchDataButton() {
    val (size, setSize) = remember { mutableStateOf(DEFAULT_CATEGORY_SIZE) }
    val (topics, setTopics) = remember { mutableStateOf(arrayOf<String>()) }
    ElevatedButton(
        modifier = Modifier.testTag("OKButton"),
        onClick = {
            fetchFromDB(size, setSize, topics, setTopics)
        }
    ) {
        Text("OK")
    }
    category_size = size

    if (topics.isNotEmpty() && category_size > 0) {
        val allTopics = topics.toMutableList()
        val indices = mutableListOf<Int>()
        for (i in 1..category_size) {
            var randomNb = (0..category_size).random()
            while (indices.contains(randomNb)) {
                randomNb = (0..category_size).random()
            }
            indices.add(randomNb)
        }
        selectedTopics.addAll(listOf(allTopics[indices[0]], allTopics[indices[1]], allTopics[indices[2]]))
    }
}

fun next(context: Context, dbref: DatabaseReference, gameId: String) {
    dbref.child("nb_rounds").setValue(selection)
    context.startActivity(Intent(context, TopicSelectionActivity::class.java).apply {
        putExtra("gameId", gameId)
        for (i in 0 until selectedTopics.size) {
            putExtra("topic$i", selectedTopics[i])
        }
    })
}

fun fetchFromDB(size: Int, setSize: (topics: Int) -> Unit, topics: Array<String>, setTopics: (topics: Array<String>) -> Unit) {
    val dbrefTopics = Firebase.database.getReference("Topics/$selectedCategory")

    // Fetch the number of topics present in the given category
    dbrefTopics.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val fetchedSize = dataSnapshot.childrenCount.toInt() - 1
            setSize(fetchedSize)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            throw databaseError.toException()
        }
    })
    fetchTopics(topics, setTopics)
}

fun fetchTopics(topics: Array<String>, setTopics: (topics: Array<String>) -> Unit) {
    val dbrefTopics = Firebase.database.getReference("Topics/$selectedCategory")

    // Fetches topics from the database
    dbrefTopics.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val fetchedTopics = snapshot.getValue<ArrayList<String>>()!!
                setTopics(fetchedTopics.toTypedArray())
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            throw databaseError.toException()
        }
    })
}

@Composable
fun GameOptionsScreen(dbref: DatabaseReference, gameId: String) {
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
        FetchDataButton()
        NextButton(dbref, gameId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        BackButton()
    }
}
