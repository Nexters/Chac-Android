package com.chac.data.album.embedding.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chac.domain.album.embedding.repository.EmbeddingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class EmbeddingIndexingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val embeddingRepository: EmbeddingRepository,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        if (!hasReadMediaPermission()) {
            Timber.d("Embedding indexing skipped: media permission is not granted.")
            return Result.success()
        }

        return runCatching {
            embeddingRepository.syncImages()
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { throwable ->
                Timber.e(throwable, "Embedding indexing worker failed.")
                Result.retry()
            },
        )
    }

    private fun hasReadMediaPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
