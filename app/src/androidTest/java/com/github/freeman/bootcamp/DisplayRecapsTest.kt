package com.github.freeman.bootcamp

import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayRecapsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setTheContentBefore(){
        composeRule.setContent {
            val context = LocalContext.current
            var userId = Firebase.auth.uid
            val dbRef = Firebase.database.reference

            // Checks if it is the first time launching the app by looking if a profile exists.
            // If no profile exists, sign in anonymously and creates a profile
            dbRef
                .child(context.getString(R.string.profiles_path))
                .child(userId.toString())
                .child(context.getString(R.string.username_path))
                .get()
                .addOnCompleteListener {

                    // If no profile exists
                    if (it.result.value == "" || it.result.value == null) {
                        // sign in anonymously
                        Firebase.auth.signInAnonymously().addOnCompleteListener(context as Activity) { task ->
                            if (task.isSuccessful) {
                                userId = Firebase.auth.uid.toString()
                                FirebaseUtilities.createProfile(context, userId!!, "Guest")
                            }
                        }
                    }
                }

            BootcampComposeTheme {
                MainMenuScreen()
            }
        }

        composeRule.onNodeWithTag("displayRecapsButton").performClick()
    }

    @Test
    fun topAppBarIsDisplayed() {
        composeRule.onNodeWithTag("topAppbarDisplayRecaps").assertIsDisplayed()
    }
}