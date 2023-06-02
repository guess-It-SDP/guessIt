package com.github.freeman.bootcamp.videocall

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoScreenTest {

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

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun VideoScreenInstantiationWorks() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            VideoScreen(
                roomName = "1",
                navController::navigateUp,
                testing = true
            )
        }
    }
}