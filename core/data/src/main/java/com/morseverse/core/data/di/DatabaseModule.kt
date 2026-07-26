package com.morseverse.core.data.di

import android.content.Context
import androidx.room.Room
import com.morseverse.core.data.database.MorseVerseDatabase
import com.morseverse.core.data.database.dao.*
import com.morseverse.core.data.repository.MorseRepositoryImpl
import com.morseverse.core.domain.repository.MorseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MorseVerseDatabase {
        return Room.databaseBuilder(
            context,
            MorseVerseDatabase::class.java,
            "morseverse_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCharacterProgressDao(db: MorseVerseDatabase): CharacterProgressDao =
        db.characterProgressDao()

    @Provides
    fun providePracticeSessionDao(db: MorseVerseDatabase): PracticeSessionDao =
        db.practiceSessionDao()

    @Provides
    fun provideCharacterResultDao(db: MorseVerseDatabase): CharacterResultDao =
        db.characterResultDao()

    @Provides
    fun provideDailyStatsDao(db: MorseVerseDatabase): DailyStatsDao =
        db.dailyStatsDao()

    @Provides
    fun provideUserProfileDao(db: MorseVerseDatabase): UserProfileDao =
        db.userProfileDao()

    @Provides
    fun provideAchievementDao(db: MorseVerseDatabase): AchievementDao =
        db.achievementDao()

    @Provides
    fun provideTranslationHistoryDao(db: MorseVerseDatabase): TranslationHistoryDao =
        db.translationHistoryDao()

    @Provides
    fun provideStoryProgressDao(db: MorseVerseDatabase): StoryProgressDao =
        db.storyProgressDao()

    @Provides
    fun provideKochProgressDao(db: MorseVerseDatabase): KochProgressDao =
        db.kochProgressDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMorseRepository(impl: MorseRepositoryImpl): MorseRepository
}
