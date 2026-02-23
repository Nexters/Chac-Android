package com.chac.data.album.embedding.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.chac.domain.album.embedding.EmbeddingIndexingWorkScheduler
import com.chac.domain.album.embedding.model.EmbeddingIndexingWorkState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class EmbeddingIndexingWorkerSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : EmbeddingIndexingWorkScheduler {
    override fun scheduleIndexing() {
        val workManager = WorkManager.getInstance(context)
        val workRequest = OneTimeWorkRequestBuilder<EmbeddingIndexingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest,
        )
    }

    override fun observeIndexingWorkState(): Flow<EmbeddingIndexingWorkState> = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkFlow(WORK_NAME)
        .map { workInfos ->
            val target = workInfos.firstOrNull { !it.state.isFinished } ?: workInfos.lastOrNull()
            target?.state.toDomainModel()
        }
        .distinctUntilChanged()

    override fun cancelIndexing() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        private const val WORK_NAME = "embedding_indexing_work"
    }
}

private fun WorkInfo.State?.toDomainModel(): EmbeddingIndexingWorkState = when (this) {
    WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> EmbeddingIndexingWorkState.Enqueued
    WorkInfo.State.RUNNING -> EmbeddingIndexingWorkState.Running
    WorkInfo.State.SUCCEEDED -> EmbeddingIndexingWorkState.Succeeded
    WorkInfo.State.FAILED -> EmbeddingIndexingWorkState.Failed
    WorkInfo.State.CANCELLED -> EmbeddingIndexingWorkState.Cancelled
    null -> EmbeddingIndexingWorkState.Idle
}
