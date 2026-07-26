package com.morseverse.app.di

import com.morseverse.core.common.utils.MorseAudioEngine
import com.morseverse.core.domain.repository.MorseRepository
import com.morseverse.core.domain.usecases.CalculateMastery
import com.morseverse.core.domain.usecases.CalculateXp
import com.morseverse.core.domain.usecases.GeneratePracticeChallenge
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGeneratePracticeChallenge(
        repository: MorseRepository
    ): GeneratePracticeChallenge = GeneratePracticeChallenge(repository)

    @Provides
    @Singleton
    fun provideCalculateMastery(): CalculateMastery = CalculateMastery()

    @Provides
    @Singleton
    fun provideCalculateXp(): CalculateXp = CalculateXp()

    @Provides
    @Singleton
    fun provideMorseAudioEngine(): MorseAudioEngine = MorseAudioEngine()
}
