package com.chac.domain.album.embedding.usecase

import com.chac.domain.album.embedding.EmbeddingIndexingWorkScheduler
import com.chac.domain.album.embedding.model.EmbeddingIndexingWorkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEmbeddingIndexingWorkStateUseCase @Inject constructor(
    private val indexingWorkScheduler: EmbeddingIndexingWorkScheduler,
) {
    operator fun invoke(): Flow<EmbeddingIndexingWorkState> = indexingWorkScheduler.observeIndexingWorkState()
}
