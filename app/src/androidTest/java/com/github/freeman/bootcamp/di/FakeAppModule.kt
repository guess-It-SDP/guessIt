package com.github.freeman.bootcamp.di

import android.content.Context
import com.github.freeman.bootcamp.recorder.AndroidAudioPlayer
import com.github.freeman.bootcamp.recorder.DistantAudioPlayer
import com.github.freeman.bootcamp.recorder.DistantAudioRecorder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
class FakeAppModule {
    @Provides
    fun provideAudioRecorder(@ApplicationContext context: Context):DistantAudioRecorder = FakeAudioRecorder()

    @Provides
    fun provideAudioPlayer(@ApplicationContext context: Context):DistantAudioPlayer = FakeAudioPlayer()

}