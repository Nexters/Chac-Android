package com.chac.domain.album.embedding.model

data class PhotoSearchResult(
    val id: Long,
    val uri: String,
    val score: Float,
)
