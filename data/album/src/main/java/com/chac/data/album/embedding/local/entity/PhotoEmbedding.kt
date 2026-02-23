package com.chac.data.album.embedding.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_embeddings")
internal data class PhotoEmbedding(
    @PrimaryKey
    val id: Long,
    val uri: String,
    @ColumnInfo(name = "embedding")
    val embedding: String,
    val timestamp: Long,
)
