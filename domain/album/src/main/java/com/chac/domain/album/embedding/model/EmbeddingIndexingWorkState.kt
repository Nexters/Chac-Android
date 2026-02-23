package com.chac.domain.album.embedding.model

enum class EmbeddingIndexingWorkState {
    Idle,
    Enqueued,
    Running,
    Succeeded,
    Failed,
    Cancelled,
}
