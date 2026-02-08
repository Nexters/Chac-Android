package com.chac.feature.album.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * "모든 사진" 갤러리 화면 라우트
 *
 * @param viewModel 갤러리 화면의 뷰모델
 * @param onLongClickMediaItem 미디어 아이템의 롱클릭 이벤트 콜백
 * @param onClickNext 하단 CTA("다음") 클릭 이벤트 콜백
 * @param onClickBack 뒤로가기 버튼 클릭 이벤트 콜백
 */
@Composable
fun GalleryAllPhotosRoute(
    viewModel: GalleryViewModel = hiltViewModel(),
    onLongClickMediaItem: (Long, Long) -> Unit,
    onClickNext: (LongArray, String) -> Unit,
    onClickBack: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel) {
        viewModel.initializeAllPhotos()
    }

    GalleryScreen(
        uiState = uiState,
        clusterId = uiState.cluster.id,
        onToggleMedia = viewModel::toggleSelection,
        onClickSelectAll = { selected: Boolean ->
            if (selected) {
                viewModel.selectAll()
            } else {
                viewModel.clearSelection()
            }
        },
        onClickNext = {
            val selectedIds = viewModel.getSelectedMediaList().map { it.id }.toLongArray()
            if (selectedIds.isEmpty()) return@GalleryScreen
            onClickNext(selectedIds, "모든 사진")
        },
        onLongClickMediaItem = { _, mediaId ->
            onLongClickMediaItem(uiState.cluster.id, mediaId)
        },
        onClickBack = onClickBack,
    )
}
