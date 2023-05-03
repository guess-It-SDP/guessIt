package com.github.freeman.bootcamp.recorder


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import com.github.freeman.bootcamp.di.AppModule
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity.Companion.PLAY_BUTTON
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity.Companion.START_RECORDING_BUTTON
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity.Companion.STOP_PLAYING
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity.Companion.STOP_RECORDING_BUTTON
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep
import javax.inject.Inject

@HiltAndroidTest
class AudioRecordingTest {


    @get:Rule(order= 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order =1 )
    var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

    @get:Rule(order=2)
    val composeRule =  createAndroidComposeRule<AudioRecordingActivity>()


    @Test
    fun playButtonTextIsCorrect() {
        node("play_button").assertTextContains(PLAY_BUTTON)
    }
    @Test
    fun stopButtonTextIsCorrect() {
        node("stop_button").assertTextContains(STOP_PLAYING)
    }

    @Test
    fun startRecordingButtonTextIsCorrect() {
        node("start_recording_button").assertTextContains(START_RECORDING_BUTTON)
    }

    @Test
    fun stopRecordingButtonTextIsCorrect(){
        node("stop_recording_button").assertTextContains(STOP_RECORDING_BUTTON)
    }

    @Test
    fun playButtonHasClickAction() {
        node("play_button").assertHasClickAction()
    }

    @Test
    fun stopButtonHasClickAction() {
        node("stop_button").assertHasClickAction()
    }

    @Test
    fun startRecordingButtonHasClickAction() {
        node("start_recording_button").assertHasClickAction()
    }
    @Test
    fun stopRecordingHasClickAction() {
        node("stop_recording_button").assertHasClickAction()
    }

    @Test
    fun clickPlay(){
        node("play_button").performClick()
    }

    @Test
    fun clickRecord(){
        node("start_recording_button").performClick()
    }
    @Test
    fun clickStopPlay(){
        node("stop_button").performClick()
    }
    @Test
    fun clickStopRecord(){
        node("stop_recording_button").performClick()
    }


    private fun node(testTag: String): SemanticsNodeInteraction {
        return composeRule.onNodeWithTag(testTag)
    }

}