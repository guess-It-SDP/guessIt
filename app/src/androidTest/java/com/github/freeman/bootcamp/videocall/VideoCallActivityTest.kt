package com.github.freeman.bootcamp.videocall

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.freeman.bootcamp.recorder.AudioRecordingActivity
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoCallActivityTest {

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            VideoCallNavHost(navController = navController, testing = true)
        }
    }

    @Test
    fun roomScreenTextFieldIsCorrectAndDisplayed() {
        composeTestRule.onNode(hasTestTag("room_screen_text_field"))
            .assertTextContains("Enter a room name")
            .assertIsDisplayed()
    }

    @Test
    fun roomScreenButtonTextIsCorrectAndDisplayedAndClickable() {
        composeTestRule.onNode(hasTestTag("room_screen_button")).assertTextContains("Join")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun videoCallWithEmptyTextFieldDoesNotNavigatesAndDisplayCorrectText() {
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "room_screen")
        composeTestRule.onNode(hasTestTag("room_screen_text_field")).performTextInput("");
        ViewActions.closeSoftKeyboard()
        composeTestRule.onNode(hasTestTag("room_screen_button")).performClick()
        composeTestRule.onNode(hasTestTag("room_screen_button")).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag("room_screen_text_field")).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag("room_screen_error_field")).assertIsDisplayed().assertTextContains("The room can't be empty")
    }

    // can not connect to agora on the continuous integration 
    /*
    @Test
    fun videoCallNavHostNavigatesToVideoScreenFromRoomScreen() {
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "room_screen")
        composeTestRule.onNode(hasTestTag("room_screen_text_field")).performTextInput("1");
        ViewActions.closeSoftKeyboard()
        composeTestRule.onNode(hasTestTag("room_screen_button")).performClick()
        composeTestRule.onNode(hasTestTag("agora_video_view")).assertIsDisplayed()
    }
    */

    @Test
    fun leavingVideoScreenCloseVideoView() {
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "room_screen")
        composeTestRule.onNode(hasTestTag("room_screen_text_field")).performTextInput("1");
        ViewActions.closeSoftKeyboard()
        roomScreenButton().performClick()
        pressBack()
        composeTestRule.onNode(hasTestTag("room_screen_button")).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag("agora_video_view")).assertDoesNotExist()
    }

    private fun  roomScreenButton(): SemanticsNodeInteraction{
       return  composeTestRule.onNode(hasTestTag("room_screen_button"))
    }

}