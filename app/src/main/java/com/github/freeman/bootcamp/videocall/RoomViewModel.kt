package com.github.freeman.bootcamp.videocall

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.freeman.bootcamp.videocall.VideoCallActivity.Companion.EMPTY_ROOM_ERROR
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 *  view model for RoomScreen display an error if no room is selected when trying to enter a video call lobby
 */
class RoomViewModel: ViewModel() {

    private val _roomName = mutableStateOf(TextFieldState())
    val roomName: State<TextFieldState> = _roomName

    private val _onJoinEvent = MutableSharedFlow<String>()
    val onJoinEvent = _onJoinEvent.asSharedFlow()

    fun onRoomEnter(name: String) {
        _roomName.value = roomName.value.copy(
            text = name
        )
    }

    fun onJoinRoom() {
        if(roomName.value.text.isBlank()) { //We did not enter a room if the text is blank
            _roomName.value = roomName.value.copy(
                error = EMPTY_ROOM_ERROR
            )
            return
        }
        viewModelScope.launch {
            _onJoinEvent.emit(roomName.value.text)
        }
    }
}