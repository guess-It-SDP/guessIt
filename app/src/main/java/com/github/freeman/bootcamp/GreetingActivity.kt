package com.github.freeman.bootcamp

import android.os.Bundle
<<<<<<< HEAD
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract


class GreetingActivity : AppCompatActivity() {


=======
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

class GreetingActivity : ComponentActivity() {
>>>>>>> d4d9d1fc8240ca56414164d8936bc0108fc4f7e6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nameText = intent.getStringExtra("name").toString()
        setContent {
            BootcampComposeTheme {
                Greeting(nameText)
            }
        }
    }
}

@Composable
fun Greeting(name: String){
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("greeting"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello, $name!")
        }
    }

}