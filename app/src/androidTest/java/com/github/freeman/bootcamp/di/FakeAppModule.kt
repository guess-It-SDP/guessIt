package com.github.freeman.bootcamp.di

import android.content.Context
import com.github.freeman.bootcamp.recorder.AndroidAudioPlayer
import com.github.freeman.bootcamp.recorder.DistantAudioRecorder
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
abstract class FakeAnalyticsModule {

    @Singleton
    @Binds
    abstract fun provideAudioRecorder(
        fakeAudioPlayer: FakeAudioPlayer
    ): DistantAudioRecorder

    @Singleton
    @Binds
    abstract fun provideAudioPlayer(
        fakeAudioPlayer: FakeAudioPlayer
    ): DistantAudioRecorder

}