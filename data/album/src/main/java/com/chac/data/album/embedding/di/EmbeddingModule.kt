package com.chac.data.album.embedding.di

import com.chac.data.album.embedding.repository.EmbeddingRepositoryImpl
import com.chac.data.album.embedding.worker.EmbeddingIndexingWorkerSchedulerImpl
import com.chac.domain.album.embedding.EmbeddingIndexingWorkScheduler
import com.chac.domain.album.embedding.repository.EmbeddingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface EmbeddingModule {
    @Binds
    @Singleton
    fun bindEmbeddingRepository(impl: EmbeddingRepositoryImpl): EmbeddingRepository

    @Binds
    @Singleton
    fun bindEmbeddingIndexingWorkScheduler(impl: EmbeddingIndexingWorkerSchedulerImpl): EmbeddingIndexingWorkScheduler
}
