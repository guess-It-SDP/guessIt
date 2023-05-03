package com.github.freeman.bootcamp.di

import androidx.compose.runtime.Composable
import com.github.freeman.bootcamp.videocall.VideoScreen2

class VideoScreenProviderImpl: VideoScreenProvider {
    @Composable
    override fun provideVideosScreenView(roomName: String) : Unit { return VideoScreen2(roomName = roomName, testing = false)
    }
}