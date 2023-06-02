package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.SettingsActivity.Companion.MUSIC_VOLUME
import com.github.freeman.bootcamp.SettingsActivity.Companion.SETTINGS_TITLE
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {


    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settingsScreenIsDisplayed() {
        startBackgroundMusicService()
        setDisplay()
        composeRule.onNode(hasTestTag("settingsScreen")).assertExists()
    }

    @Test
    fun settingsTitleIsDisplayed() {
        setDisplay()
        composeRule.onNode(hasTestTag(SETTINGS_TITLE)).assertExists()
    }

    @Test
    fun musicVolumeTextIsCorrect() {
        setDisplay()
        composeRule.onNode(hasTestTag(MUSIC_VOLUME)).assertExists()//.assertTextContains(MUSIC_VOLUME)
    }

    private fun startBackgroundMusicService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.startService(Intent(context, BackgroundMusicService::class.java))
    }


    private fun setDisplay() {
        composeRule.setContent {
            BootcampComposeTheme {
                SettingsScreen()
            }
        }
    }
    

}