package com.github.freeman.bootcamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme

/**
 * This class is for demonstrating and for debugging
 * The MainMenuActivity will be the main screen of the app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {

            }
        }
    }
}

