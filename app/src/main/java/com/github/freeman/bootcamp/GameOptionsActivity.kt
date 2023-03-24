package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.DEFAULT_CATEGORY_SIZE
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NB_ROUNDS
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NEXT
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.ROUNDS_SELECTION
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.selectedTopics
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.selection
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.*
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
        const val ROUNDS_SELECTION = "Select the number of rounds"
        const val NEXT = "Next"
        const val NB_TOPICS = 3
        const val DEFAULT_CATEGORY_SIZE = 100
        val NB_ROUNDS = listOf("1", "3", "5", "7", "9")
        var selection: Int = 5
        var selectedTopics = mutableListOf<String?>()
    }
}

@Composable
fun RadioButtonsDisplay() {
    val kinds = NB_ROUNDS
    val (selected, setSelected) = remember { mutableStateOf("5") }
    RadioButtons(mItems = kinds, selected, setSelected)
    selection = Integer.parseInt(selected)
}

@Composable
fun RadioButtons(mItems: List<String>, selected: String, setSelected: (selected: String) -> Unit) {
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

fun next(context: Context, dbref: DatabaseReference, gameId: String) {
    dbref.child("nb_rounds").setValue(selection)
    context.startActivity(Intent(context, TopicSelectionActivity::class.java).apply {
        putExtra("gameId", gameId)
        for (i in 0 until selectedTopics.size) {
            putExtra("topic$i", selectedTopics[i])
        }
    })
}

@Composable
fun FetchFromDB() {
    val dbrefTopics = Firebase.database.getReference("Topics/Animals")
    val context = LocalContext.current
    var topics by remember { mutableStateOf(arrayOf<String>()) }
    var size = DEFAULT_CATEGORY_SIZE

    // Fetch the number of topics present in the given category
    dbrefTopics.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            size = dataSnapshot.childrenCount.toInt()
            Toast.makeText(context, "Size: $size", Toast.LENGTH_SHORT).show()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            throw databaseError.toException()
        }
    })

    // Fetches topics from the database
    dbrefTopics.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val fetchedTopics = snapshot.getValue<ArrayList<String>>()!!
                topics = fetchedTopics.toTypedArray()

                val indices = mutableListOf<Int>()
                for (i in 1..3) {
                    val randomNb = (0..size).random()
                    indices.add(randomNb)
                }
                selectedTopics.addAll(listOf(topics[indices[0]], topics[indices[1]], topics[indices[2]]))
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
            modifier = Modifier.testTag("roundsSelection"),
            text = ROUNDS_SELECTION
        )
        RadioButtonsDisplay()
        NextButton(dbref, gameId)
        FetchFromDB()
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
