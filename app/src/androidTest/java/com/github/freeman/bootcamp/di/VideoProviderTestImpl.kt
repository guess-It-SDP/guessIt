package com.github.freeman.bootcamp.di

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.freeman.bootcamp.videocall.VideoScreen2

class VideoProviderTestImpl : VideoScreenProvider {
    @Composable
    override fun provideVideosScreenView(roomName: String) = Box(modifier = Modifier.fillMaxWidth(0.2f).fillMaxSize().testTag("testVideoScreen"))
}