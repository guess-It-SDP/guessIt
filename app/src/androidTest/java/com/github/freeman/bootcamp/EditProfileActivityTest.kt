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
class EditProfileActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            val displayName = remember { mutableStateOf("Chris P. Bacon") }
            val profilePicBitmap = remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }

            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopAppbarSettings(context = LocalContext.current)
                    EditUserDetails(displayName = displayName, profilePic = profilePicBitmap)
                }
            }
        }
    }


    @Test
    fun editUserDetailsIsDisplayed() {
        composeRule.onNodeWithTag("editUserDetails").assertIsDisplayed()
    }

    @Test
    fun customDialogIsDisplayed() {
        composeRule.onNodeWithText("Name").performClick()
        composeRule.onNodeWithTag("customDialog").assertIsDisplayed()
    }

    @Test
    fun editOptionsItemStyleIsDisplayed() {
        composeRule.onNodeWithTag("editOptionsItemStyle").assertIsDisplayed()
    }

}