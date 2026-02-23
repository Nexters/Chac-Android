package com.chac.data.album.embedding.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chac.data.album.embedding.local.dao.PhotoDao
import com.chac.data.album.embedding.local.entity.PhotoEmbedding

@Database(
    entities = [PhotoEmbedding::class],
    version = 1,
    exportSchema = false,
)
internal abstract class EmbeddingDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object {
        const val DB_NAME = "embedding.db"
    }
}
