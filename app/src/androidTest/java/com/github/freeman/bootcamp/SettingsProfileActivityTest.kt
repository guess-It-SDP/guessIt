package com.github.freeman.bootcamp

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsProfileActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            val context = LocalContext.current

            val displayName = remember { mutableStateOf("Chris P. Bacon") }
            val profilePicBitmap = remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }
            val email = remember { mutableStateOf("em@il.com") }


            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopAppbarSettings(context = context)
                    Profile(displayName = displayName, profilePic = profilePicBitmap, email = email)
                }
            }
        }
    }


    @Test
    fun topAppBarProfileIsDisplayed() {
        composeRule.onNodeWithTag("topAppbarProfile").assertIsDisplayed()
    }

    @Test
    fun profileIsDisplayed() {
        composeRule.onNodeWithTag("profile").assertIsDisplayed()
    }

    @Test
    fun userDetailsIsDisplayed() {
        composeRule.onNodeWithTag("userDetails").assertIsDisplayed()
    }

    @Test
    fun optionsItemStyleIsDisplayed() {
        composeRule.onAllNodesWithTag("optionsItemStyle").assertAll(isEnabled())
    }
}