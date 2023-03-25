package com.github.freeman.bootcamp.videocall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// this is key to link the session to an agora account
const val APP_ID = "46961364e2c74b4dbcc4d2ead7a09bee"

/**
 * Handles navigation between RoomScreen and VideoScreen in order to make a video Call
 */
@ExperimentalUnsignedTypes
class VideoCallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.padding(16.dp)
            ) {
                val navController = rememberNavController()
                VideoCallNavHost(
                    navController = navController,
                    startDestination = "room_screen",
                    modifier = Modifier.testTag("video_call_nav_host")
                )
            }
        }
    }
}

@Composable
fun  VideoCallNavHost(
    modifier: Modifier = Modifier.testTag("video_call_nav_host"),
    navController: NavHostController = rememberNavController(),
    startDestination: String = "room_screen",
    testing: Boolean = false
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = "room_screen") {
            RoomScreen(onNavigate = navController::navigate)
        }
        composable(
            route = "video_screen/{roomName}",
            arguments = listOf(
                navArgument(name = "roomName") {
                    type = NavType.StringType
                }
            )
        ) {
            val roomName = it.arguments?.getString("roomName") ?: return@composable
            VideoScreen(
                roomName = roomName,
                onNavigateUp = navController::navigateUp,
                testing = testing
            )
        }
    }
}