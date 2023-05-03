package com.github.freeman.bootcamp.di

import androidx.compose.runtime.Composable
import com.github.freeman.bootcamp.videocall.VideoScreen2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    fun provideVideoScreenProvider() : VideoScreenProvider = VideoScreenProviderImpl()
}