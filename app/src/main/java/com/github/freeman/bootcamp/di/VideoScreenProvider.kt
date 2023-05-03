package com.github.freeman.bootcamp.di

import androidx.compose.runtime.Composable
import com.github.freeman.bootcamp.videocall.VideoScreen2

interface VideoScreenProvider {
    @Composable
    fun provideVideosScreenView(roomName: String) : Unit
    }
