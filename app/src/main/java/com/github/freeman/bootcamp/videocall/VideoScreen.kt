package com.github.freeman.bootcamp.videocall

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraVideoViewer

/**
 * screen of the video call, use agora.io
 *
 * @param roomName the number of the lobby that will be entered
 * @param onNavigateUp navigation controller
 * @param viewModel the view model of the video screen
 * @param testing false = normal mode, true = tests are running
 */
@ExperimentalUnsignedTypes
@Composable
fun VideoScreen(
    roomName: String,
    onNavigateUp: () -> Unit = {},
    viewModel: VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    testing: Boolean
) {
    var agoraView: AgoraVideoViewer? = null // used to leave the video call when calling on return
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            viewModel.onPermissionsResult(
                acceptedAudioPermission = perms[Manifest.permission.RECORD_AUDIO] == true,
                acceptedCameraPermission = perms[Manifest.permission.CAMERA] == true,
            )
        }
    )
    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
            )
        )
    }
    BackHandler {
        agoraView?.leaveChannel()
        onNavigateUp()
    }
    if(viewModel.hasAudioPermission.value && viewModel.hasCameraPermission.value && !testing) {
        AndroidView(
            factory = {
                AgoraVideoViewer(
                    it, connectionData = AgoraConnectionData(
                        appId = APP_ID
                    ),style = AgoraVideoViewer.Style.GRID
                ).also {
                    it.join(roomName)
                    agoraView = it
                }
            },
            modifier = Modifier.fillMaxSize().testTag("agora_video_view")
        )
    }
}