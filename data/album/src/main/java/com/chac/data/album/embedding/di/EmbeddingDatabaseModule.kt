package com.chac.data.album.embedding.di

import android.content.Context
import androidx.room.Room
import com.chac.data.album.embedding.local.EmbeddingDatabase
import com.chac.data.album.embedding.local.dao.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object EmbeddingDatabaseModule {
    @Provides
    @Singleton
    fun provideEmbeddingDatabase(
        @ApplicationContext context: Context,
    ): EmbeddingDatabase = Room.databaseBuilder(
        context,
        EmbeddingDatabase::class.java,
        EmbeddingDatabase.DB_NAME,
    ).build()

    @Provides
    fun providePhotoDao(database: EmbeddingDatabase): PhotoDao = database.photoDao()
}
