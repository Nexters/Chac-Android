package com.chac.domain.album.embedding.usecase

import com.chac.domain.album.embedding.EmbeddingIndexingWorkScheduler
import javax.inject.Inject

class CancelEmbeddingIndexingUseCase @Inject constructor(
    private val indexingWorkScheduler: EmbeddingIndexingWorkScheduler,
) {
    operator fun invoke() {
        indexingWorkScheduler.cancelIndexing()
    }
}
