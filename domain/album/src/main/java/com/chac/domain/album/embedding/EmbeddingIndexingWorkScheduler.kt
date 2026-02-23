package com.chac.domain.album.embedding

import com.chac.domain.album.embedding.model.EmbeddingIndexingWorkState
import kotlinx.coroutines.flow.Flow

interface EmbeddingIndexingWorkScheduler {
    fun scheduleIndexing()

    fun observeIndexingWorkState(): Flow<EmbeddingIndexingWorkState>

    fun cancelIndexing()
}
