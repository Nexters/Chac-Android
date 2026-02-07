package com.chac.feature.album.gallery.component

import androidx.lifecycle.ViewModel
import com.chac.feature.album.model.MediaClusterUiModel
import com.chac.feature.album.model.MediaUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** 미디어 미리보기 화면 상태를 제공하는 ViewModel */
@HiltViewModel
class MediaPreviewViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<MediaPreviewUiState>(MediaPreviewUiState.Loading)
    val uiState: StateFlow<MediaPreviewUiState> = _uiState.asStateFlow()

    /**
     * 클러스터와 미디어 ID로 미리보기 상태를 초기화한다.
     *
     * @param cluster 클러스터
     * @param mediaId 최초 표시할 미디어 식별자
     */
    fun initialize(cluster: MediaClusterUiModel, mediaId: Long) {
        if (_uiState.value is MediaPreviewUiState.Ready) return

        val initialIndex = cluster.mediaList
            .indexOfFirst { it.id == mediaId }
            .coerceAtLeast(0)

        _uiState.value = MediaPreviewUiState.Ready(
            mediaList = cluster.mediaList,
            initialIndex = initialIndex,
            address = cluster.address,
        )
    }
}

/** 미디어 미리보기 화면 UI 상태 */
sealed interface MediaPreviewUiState {
    data object Loading : MediaPreviewUiState

    data class Ready(
        val mediaList: List<MediaUiModel>,
        val initialIndex: Int,
        val address: String,
    ) : MediaPreviewUiState
}
