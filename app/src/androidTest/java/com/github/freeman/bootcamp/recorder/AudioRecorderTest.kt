package com.github.freeman.bootcamp.recorder


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity.Companion.PLAY_BUTTON
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity.Companion.START_RECORDING_BUTTON
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity.Companion.STOP_PLAYING
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity.Companion.STOP_RECORDING_BUTTON
import org.junit.Rule
import org.junit.Test

class AudioRecordingTest {
    @get:Rule
    var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

    @get:Rule
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

    private fun node(testTag: String): SemanticsNodeInteraction {
        return composeRule.onNodeWithTag(testTag)
    }

}