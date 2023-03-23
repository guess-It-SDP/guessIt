package com.github.freeman.bootcamp.recorder


import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test


class AudioRecordingTest {
    @get:Rule
    var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

    @get:Rule
    val composeRule =  createAndroidComposeRule<AudioRecordingActivity>()



    @Test
    fun playButtonTextIsCorrect() {
        composeRule.onNode(hasTestTag("play_button")).assertTextContains("Play")
    }
    @Test
    fun stopButtonTextIsCorrect() {
        composeRule.onNode(hasTestTag("stop_button")).assertTextContains("Stop playing")
    }

    @Test
    fun startRecordingButtonTextIsCorrect() {
        composeRule.onNode(hasTestTag("start_recording_button")).assertTextContains("Start recording")
    }

    @Test
    fun stopRecordingButtonTextIsCorrect(){
        composeRule.onNode(hasTestTag("stop_recording_button")).assertTextContains("Stop recording")
    }

    @Test
    fun playButtonHasClickAction() {
        composeRule.onNode(hasTestTag("play_button")).assertHasClickAction()
    }

    @Test
    fun stopButtonHasClickAction() {
        composeRule.onNode(hasTestTag("stop_button")).assertHasClickAction()
    }

    @Test
    fun startRecordingButtonHasClickAction() {
        composeRule.onNode(hasTestTag("start_recording_button")).assertHasClickAction()
    }
    @Test
    fun stopRecordingHasClickAction() {
        composeRule.onNode(hasTestTag("stop_recording_button")).assertHasClickAction()
    }

}