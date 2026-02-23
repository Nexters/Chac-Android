package com.chac.domain.album.embedding.usecase

import com.chac.domain.album.embedding.model.PhotoSearchResult
import com.chac.domain.album.embedding.repository.EmbeddingRepository
import javax.inject.Inject

class SearchPhotoEmbeddingsUseCase @Inject constructor(
    private val embeddingRepository: EmbeddingRepository,
) {
    suspend operator fun invoke(
        query: String,
        topK: Int = 30,
    ): List<PhotoSearchResult> = embeddingRepository.searchByText(
        query = query,
        topK = topK,
    )
}
