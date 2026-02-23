package com.chac.data.album.embedding.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.chac.data.album.embedding.local.dao.PhotoDao
import com.chac.data.album.embedding.local.entity.PhotoEmbedding
import com.chac.data.album.embedding.model.OnnxModelManager
import com.chac.data.album.embedding.vector.VectorUtils
import com.chac.data.album.media.MediaDataSource
import com.chac.domain.album.embedding.model.PhotoEmbeddingSnapshot
import com.chac.domain.album.embedding.model.PhotoSearchResult
import com.chac.domain.album.embedding.repository.EmbeddingRepository
import com.chac.domain.album.media.model.MediaSortOrder
import com.chac.domain.album.media.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import androidx.core.net.toUri

internal class EmbeddingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val photoDao: PhotoDao,
    private val onnxModelManager: OnnxModelManager,
    private val mediaDataSource: MediaDataSource,
) : EmbeddingRepository {
    override suspend fun syncImages() = withContext(Dispatchers.IO) {
        val startedAt = System.currentTimeMillis()
        val images = mediaDataSource.getMedia(
            startTime = 0L,
            endTime = System.currentTimeMillis(),
            mediaType = MediaType.IMAGE,
            mediaSortOrder = MediaSortOrder.NEWEST_FIRST,
        )

        var alreadyCachedCount = 0
        var cachedSuccessCount = 0
        var decodeFailedCount = 0
        var failedCount = 0

        images.forEach { media ->
            if (photoDao.exists(media.id)) {
                alreadyCachedCount += 1
                return@forEach
            }

            when (
                cacheEmbeddingInternal(
                    id = media.id,
                    uri = media.uriString,
                    timestamp = media.dateTaken,
                )
            ) {
                CacheEmbeddingResult.CACHED_SUCCESS -> cachedSuccessCount += 1
                CacheEmbeddingResult.DECODE_FAILED -> decodeFailedCount += 1
                CacheEmbeddingResult.FAILED -> failedCount += 1
            }
        }

        val elapsedMs = System.currentTimeMillis() - startedAt
        val processTargetCount = images.size - alreadyCachedCount
        Timber.tag("12341234").i(
            "Embedding sync summary - total=%d, alreadyCached=%d, target=%d, " +
                "cachedSuccess=%d, decodeFailed=%d, failed=%d, elapsedMs=%d",
            images.size,
            alreadyCachedCount,
            processTargetCount,
            cachedSuccessCount,
            decodeFailedCount,
            failedCount,
            elapsedMs,
        )
    }

    override suspend fun cacheEmbedding(
        id: Long,
        uri: String,
        timestamp: Long,
    ) {
        cacheEmbeddingInternal(
            id = id,
            uri = uri,
            timestamp = timestamp,
        )
    }

    private suspend fun cacheEmbeddingInternal(
        id: Long,
        uri: String,
        timestamp: Long,
    ): CacheEmbeddingResult = withContext(Dispatchers.IO) {
        val bitmap = loadBitmap(uri)
        if (bitmap == null) {
            Timber.e("Failed to decode bitmap from uri: %s. Skipping embedding cache.", uri)
            return@withContext CacheEmbeddingResult.DECODE_FAILED
        }

        try {
            runCatching {
                val embedding = onnxModelManager.encodeImage(bitmap)
                photoDao.upsert(
                    PhotoEmbedding(
                        id = id,
                        uri = uri,
                        embedding = serializeEmbedding(embedding),
                        timestamp = timestamp,
                    ),
                )
            }.onFailure { throwable ->
                Timber.e(throwable, "Failed to cache embedding for uri: %s", uri)
            }.fold(
                onSuccess = { CacheEmbeddingResult.CACHED_SUCCESS },
                onFailure = { CacheEmbeddingResult.FAILED },
            )
        } finally {
            bitmap.recycle()
        }
    }

    override suspend fun searchByText(
        query: String,
        topK: Int,
    ): List<PhotoSearchResult> = withContext(Dispatchers.Default) {
        if (query.isBlank()) return@withContext emptyList()

        val queryEmbedding = onnxModelManager.encodeText(query)
        val limit = topK.coerceAtLeast(1)

        photoDao.getAll()
            .asSequence()
            .mapNotNull { entity ->
                val embedding = deserializeEmbedding(entity.embedding) ?: return@mapNotNull null
                val score = VectorUtils.calculateCosineSimilarity(queryEmbedding, embedding)
                PhotoSearchResult(
                    id = entity.id,
                    uri = entity.uri,
                    score = score,
                )
            }
            .sortedByDescending { item -> item.score }
            .take(limit)
            .toList()
    }

    override suspend fun getCachedEmbeddings(): List<PhotoEmbeddingSnapshot> = withContext(Dispatchers.IO) {
        photoDao.getAll().mapNotNull { entity ->
            val embedding = deserializeEmbedding(entity.embedding) ?: return@mapNotNull null
            PhotoEmbeddingSnapshot(
                id = entity.id,
                uri = entity.uri,
                embedding = embedding,
                timestamp = entity.timestamp,
            )
        }
    }

    private fun loadBitmap(uri: String): Bitmap? = runCatching {
        context.contentResolver.openInputStream(uri.toUri())?.use { input ->
            BitmapFactory.decodeStream(input)
        }
    }.onFailure { throwable ->
        Timber.e(throwable, "Failed to load bitmap from uri: %s", uri)
    }.getOrNull()

    private fun serializeEmbedding(embedding: FloatArray): String {
        val jsonArray = JSONArray()
        embedding.forEach { value ->
            jsonArray.put(value.toDouble())
        }
        return jsonArray.toString()
    }

    private fun deserializeEmbedding(raw: String): FloatArray? = runCatching {
        val jsonArray = JSONArray(raw)
        FloatArray(jsonArray.length()) { index ->
            jsonArray.getDouble(index).toFloat()
        }
    }.onFailure { throwable ->
        Timber.e(throwable, "Failed to deserialize embedding JSON.")
    }.getOrNull()

    private enum class CacheEmbeddingResult {
        CACHED_SUCCESS,
        DECODE_FAILED,
        FAILED,
    }
}
