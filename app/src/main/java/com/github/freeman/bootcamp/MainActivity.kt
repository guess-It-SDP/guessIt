package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import android.view.ActionProvider
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
=======
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
>>>>>>> d4d9d1fc8240ca56414164d8936bc0108fc4f7e6

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                MainScreen()
            }
        }
    }
}

// this class is to store the value of the text field
// in order to use it in other Composable
class TextFieldState{
    var text: String by mutableStateOf("")
}

fun greet(context: Context, name: String) {
    if (!name.isEmpty()) {
        context.startActivity(Intent(context, GreetingActivity::class.java).apply {
            putExtra("name", name)
        })
    }

}

@Composable
fun GreetingInput(msg : TextFieldState = remember { TextFieldState() }) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    OutlinedTextField(
        modifier = Modifier.testTag("greetingInput"),
        value = text,
        label = {
            Text(text = "Enter Your Name")
        },
        onValueChange = {
            text = it
            msg.text = it.text
        }
    )
}

@Composable
fun GreetingButton(msg : TextFieldState = remember { TextFieldState() }) {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.testTag("greetingButton"),
        onClick = {
            greet(context, msg.text)

        }
    ) {
        Text("Greet me!")
    }
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mainScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textState = remember { TextFieldState() }
        GreetingInput(textState)
        GreetingButton(textState)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}

@Preview
@Composable
fun GreetingButtonPreview() {
    GreetingButton()
}

@Preview
@Composable
fun GreetingInputPreview() {
    GreetingInput()
}