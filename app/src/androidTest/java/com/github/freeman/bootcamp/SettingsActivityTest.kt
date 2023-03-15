package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat.startActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

// Note: all references to MainActivity have to be replaced with the main menu activity

    @get:Rule
    val composeRule = createComposeRule()

//    @get:Rule
//    val mainMenuActivity = createAndroidComposeRule<MainMenuActivity>()
//
//    @get:Rule
//    val settingsActivity = createAndroidComposeRule<SettingsActivity>()

    @Test
    fun backButtonHasClickAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.startService(Intent(context, BackgroundMusicService::class.java))
        setDisplay()
        composeRule.onNode(hasTestTag("backButton")).assertHasClickAction()
    }

//        setDisplay()
//        mainActivity.onNodeWithText("Settings").performClick()

//        Intents.init()
//
//        // Create the intent with the desired extras
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        val intent = Intent(context, SettingsActivity::class.java)
//
//        // Launch the activity using the intent
//        launchActivity<MainActivity>(intent)
//        context.startService(Intent(context, BackgroundMusicService::class.java))
//        launchActivity<SettingsActivity>(Intent(Intent.ACTION_MAIN))
//        context.startService(Intent(context, BackgroundMusicService::class.java))

//        mainActivity.activity.startActivity(Intent(Intent.ACTION_MAIN))

//        val mainAc = createAndroidComposeRule<MainActivity>().activity
//        val b = BackgroundMusicService()
//        val i = Intent(mainAc, b::class.java)
//        startService(i)


//        Intents.init()
//        composeRule.setContent {
//            BootcampComposeTheme {
//                MainScreen()
//            }
//        }
//        composeRule.onNodeWithText("Settings").performClick()
//        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
//        Intents.release()
//    }

    fun setDisplay() {
        composeRule.setContent {
            BootcampComposeTheme {
                SettingsScreen()
            }
        }
    }
}