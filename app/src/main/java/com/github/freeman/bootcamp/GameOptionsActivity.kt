package com.github.freeman.bootcamp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.NB_ROUNDS
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.ROUNDS_SELECTION
import com.github.freeman.bootcamp.GameOptionsActivity.Companion.selection
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class GameOptionsActivity : ComponentActivity() {

    private val gameId = "TestGameId01" // TODO: Create an id rng or some other way of creating unique ids
    private lateinit var myList: LinkedList<String>
    private val dbref = Firebase.database.getReference("Games/$gameId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myList = LinkedList()
        setContent {
            BootcampComposeTheme {
                GameOptionsScreen(dbref, myList)
            }
        }
    }

    companion object {
        const val ROUNDS_SELECTION = "Select the number of rounds"
        val NB_ROUNDS = listOf("1", "3", "5", "7", "9")
        var selection: String = "5"
    }
}

@Composable
fun SendToDBButton(dbref: DatabaseReference, myList: LinkedList<String>) {
    ElevatedButton(
        modifier = Modifier.testTag("sendToDBButton"),
        onClick = { send(dbref, "Test Value", myList) }
    ) {
        Text("Send to Firebase")
    }
}

@Composable
fun RadioButtonsDisplay() {
    val kinds = NB_ROUNDS
    val (selected, setSelected) = remember { mutableStateOf("") }
    Column {
        RadioButtons(mItems = kinds, selected, setSelected)
        Text(
            text = "Selected Option : $selected",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    selection = selected
    Toast.makeText(LocalContext.current, "selection: " + selection, Toast.LENGTH_LONG).show()
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
                    Text(text = item, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

fun send(dbref: DatabaseReference, testValue: String, myList: LinkedList<String>) {
    myList.add(testValue + "1")
    myList.add(testValue + "2")
    myList.add(testValue + "3")
    myList.add(testValue + "4")
    dbref.setValue(myList)
}

@Composable
fun GameOptionsScreen(dbref: DatabaseReference, myList: LinkedList<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("gameOptionsScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(ROUNDS_SELECTION)
        RadioButtonsDisplay()
        SendToDBButton(dbref, myList)
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

@Preview(showBackground = true)
@Composable
fun GameOptionsScreenPreview() {
    val gameId = "TestGameId01"
    val db = Firebase.database
    db.useEmulator("10.0.2.2", 9000)
    val dbref =  Firebase.database.getReference("Games/$gameId")
    GameOptionsScreen(dbref, LinkedList())
}