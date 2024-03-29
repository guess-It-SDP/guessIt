package com.github.freeman.bootcamp.videocall
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * viewModel for video screen, keeps audio permission and camera permission
 */
class VideoViewModel: ViewModel() {

    private val _hasAudioPermission = mutableStateOf(false)
    val hasAudioPermission: State<Boolean> = _hasAudioPermission

    private val _hasCameraPermission = mutableStateOf(false)
    val hasCameraPermission: State<Boolean> = _hasCameraPermission

    fun onPermissionsResult(
        acceptedAudioPermission: Boolean,
        acceptedCameraPermission: Boolean
    ) {
        _hasAudioPermission.value = acceptedAudioPermission
        _hasCameraPermission.value = acceptedCameraPermission
    }
}