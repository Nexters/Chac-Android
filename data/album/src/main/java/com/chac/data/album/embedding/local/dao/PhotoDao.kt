package com.chac.data.album.embedding.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chac.data.album.embedding.local.entity.PhotoEmbedding

@Dao
internal interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PhotoEmbedding)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PhotoEmbedding>)

    @Query("SELECT * FROM photo_embeddings WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PhotoEmbedding?

    @Query("SELECT EXISTS(SELECT 1 FROM photo_embeddings WHERE id = :id)")
    suspend fun exists(id: Long): Boolean

    @Query("SELECT * FROM photo_embeddings")
    suspend fun getAll(): List<PhotoEmbedding>

    @Query("DELETE FROM photo_embeddings WHERE id NOT IN (:ids)")
    suspend fun deleteNotIn(ids: List<Long>)
}
