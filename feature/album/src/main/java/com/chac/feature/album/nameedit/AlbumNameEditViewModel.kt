package com.chac.feature.album.nameedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chac.domain.album.media.model.Media
import com.chac.domain.album.media.usecase.GetAllMediaUseCase
import com.chac.domain.album.media.usecase.GetClusteredMediaStateUseCase
import com.chac.domain.album.media.usecase.SaveAlbumWithTitleUseCase
import com.chac.feature.album.navigation.AlbumNameEditSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class AlbumSaveCompletedEvent(
    val title: String,
    val savedCount: Int,
)

sealed interface AlbumNameEditUiState {
    data object Loading : AlbumNameEditUiState

    data class Ready(
        val thumbnailUriString: String?,
        val albumName: String,
        val selectedCount: Int,
        val uriStrings: List<String>,
    ) : AlbumNameEditUiState

    data class Saving(
        val thumbnailUriString: String?,
        val albumName: String,
        val selectedCount: Int,
    ) : AlbumNameEditUiState

    data class Error(
        val message: String,
    ) : AlbumNameEditUiState
}

@HiltViewModel
class AlbumNameEditViewModel @Inject constructor(
    private val getClusteredMediaStateUseCase: GetClusteredMediaStateUseCase,
    private val getAllMediaUseCase: GetAllMediaUseCase,
    private val saveAlbumWithTitleUseCase: SaveAlbumWithTitleUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AlbumNameEditUiState>(AlbumNameEditUiState.Loading)
    val uiState: StateFlow<AlbumNameEditUiState> = _uiState.asStateFlow()

    private val saveCompletedEventsChannel = Channel<AlbumSaveCompletedEvent>(capacity = Channel.BUFFERED)
    val saveCompletedEvents = saveCompletedEventsChannel.receiveAsFlow()

    private var initializeJob: Job? = null
    private var saveJob: Job? = null
    private var selectedMedia: List<Media> = emptyList()
    private var hasInitialized = false

    fun initialize(
        source: AlbumNameEditSource,
        selectedIds: LongArray,
        defaultAlbumName: String,
    ) {
        if (hasInitialized) return
        hasInitialized = true

        val ids = selectedIds.toHashSet()
        if (ids.isEmpty()) {
            _uiState.value = AlbumNameEditUiState.Error("선택된 사진이 없습니다.")
            return
        }

        initializeJob = viewModelScope.launch {
            when (source) {
                is AlbumNameEditSource.Cluster -> loadFromCluster(
                    clusterId = source.clusterId,
                    selectedIds = ids,
                    defaultAlbumName = defaultAlbumName,
                )

                AlbumNameEditSource.AllPhotos -> loadFromAllPhotos(
                    selectedIds = ids,
                    defaultAlbumName = defaultAlbumName,
                )
            }
        }
    }

    fun setAlbumName(newName: String) {
        val current = _uiState.value
        if (current !is AlbumNameEditUiState.Ready) return
        _uiState.value = current.copy(albumName = newName)
    }

    fun clearAlbumName() {
        setAlbumName("")
    }

    fun save() {
        val current = _uiState.value as? AlbumNameEditUiState.Ready ?: return
        if (saveJob?.isActive == true) return
        if (selectedMedia.isEmpty()) return

        _uiState.value = AlbumNameEditUiState.Saving(
            thumbnailUriString = current.thumbnailUriString,
            albumName = current.albumName,
            selectedCount = current.selectedCount,
        )

        saveJob = viewModelScope.launch {
            try {
                val result = runCatching {
                    saveAlbumWithTitleUseCase(
                        title = current.albumName,
                        mediaList = selectedMedia,
                    )
                }
                if (result.isSuccess) {
                    saveCompletedEventsChannel.trySend(
                        AlbumSaveCompletedEvent(
                            title = current.albumName,
                            savedCount = result.getOrNull().orEmpty().size,
                        ),
                    )
                } else {
                    Timber.e(result.exceptionOrNull(), "Failed to save album")
                    _uiState.value = current
                }
            } finally {
                saveJob = null
            }
        }
    }

    private suspend fun loadFromCluster(
        clusterId: Long,
        selectedIds: Set<Long>,
        defaultAlbumName: String,
    ) {
        getClusteredMediaStateUseCase().collect { clusters ->
            val cluster = clusters.firstOrNull { it.id == clusterId } ?: return@collect
            val media = cluster.mediaList.filter { it.id in selectedIds }
            if (media.isEmpty()) {
                _uiState.value = AlbumNameEditUiState.Error("선택된 사진을 찾을 수 없습니다.")
            } else {
                setReady(
                    albumName = defaultAlbumName,
                    media = media,
                )
            }
            initializeJob?.cancel()
        }
    }

    private suspend fun loadFromAllPhotos(
        selectedIds: Set<Long>,
        defaultAlbumName: String,
    ) {
        val all = runCatching { getAllMediaUseCase() }.getOrElse { emptyList() }
        val media = all.filter { it.id in selectedIds }
        if (media.isEmpty()) {
            _uiState.value = AlbumNameEditUiState.Error("선택된 사진을 찾을 수 없습니다.")
            return
        }
        setReady(
            albumName = defaultAlbumName,
            media = media,
        )
    }

    private fun setReady(
        albumName: String,
        media: List<Media>,
    ) {
        selectedMedia = media
        _uiState.value = AlbumNameEditUiState.Ready(
            thumbnailUriString = media.firstOrNull()?.uriString,
            albumName = albumName,
            selectedCount = media.size,
            uriStrings = media.map { it.uriString },
        )
    }
}
