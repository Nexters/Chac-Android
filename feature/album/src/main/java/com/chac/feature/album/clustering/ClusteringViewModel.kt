package com.chac.feature.album.clustering

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chac.domain.album.media.GetClusteredMediaStreamUseCase
import com.chac.feature.album.clustering.model.ClusteringUiState
import com.chac.feature.album.clustering.model.toUiModel
import com.chac.feature.album.model.ClusterUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** 클러스터링 화면 상태를 제공하는 ViewModel */
@HiltViewModel
class ClusteringViewModel
    @Inject
    constructor(
        private val getClusteredMediaStreamUseCase: GetClusteredMediaStreamUseCase,
    ) : ViewModel() {

        /** 클러스터링 화면의 상태 */
        val uiState: StateFlow<ClusteringUiState> = flow {
            var clusters = emptyList<ClusterUiModel>()

            // 초기값
            emit(ClusteringUiState.Loading(clusters))

            // 클러스터 스트림을 수집하며 로딩 상태에 누적한다.
            getClusteredMediaStreamUseCase().collect { cluster ->
                clusters = clusters + cluster.toUiModel()
                emit(ClusteringUiState.Loading(clusters))
            }

            // 클러스터링이 완료 되면 Completed 상태로 변경
            emit(ClusteringUiState.Completed(clusters))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ClusteringUiState.Loading(emptyList()),
        )
    }
