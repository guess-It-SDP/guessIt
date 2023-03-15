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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settingsScreenIsDisplayed() {
        startBackgroundMusicService()
        setDisplay()
        composeRule.onNode(hasTestTag("settingsScreen")).assertIsDisplayed()
    }

    @Test
    fun settingsTitleIsDisplayed() {
        startBackgroundMusicService()
        setDisplay()
        composeRule.onNode(hasTestTag(SETTINGS_TITLE)).assertTextContains(SETTINGS_TITLE)
    }

    @Test
    fun backButtonHasClickAction() {
        startBackgroundMusicService()
        setDisplay()
        composeRule.onNode(hasTestTag("settingsBackButton")).assertHasClickAction()
    }

    @Test
    fun musicVolumeTextIsCorrect() {
        startBackgroundMusicService()
        setDisplay()
        composeRule.onNode(hasTestTag(MUSIC_VOLUME)).assertTextContains(MUSIC_VOLUME)
    }

    @Test
    fun backButtonClosesActivity() {
        startBackgroundMusicService()
        setDisplay()
        composeRule.onNode(hasTestTag("settingsBackButton")).performClick()
        sleep(5000)
        assertEquals(false, SettingsActivity.isRunning)
    }

    @Test
    fun backgroundMusicServiceClosesOnStop() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, BackgroundMusicService::class.java)
        context.startService(intent)
        context.stopService(intent)
        sleep(5000)
        assertEquals(false, BackgroundMusicService.isRunning)
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