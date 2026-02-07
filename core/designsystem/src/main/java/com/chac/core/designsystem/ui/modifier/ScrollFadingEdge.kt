package com.chac.core.designsystem.ui.modifier

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * [LazyListState] 기반 스크롤 영역에 상/하단 페이딩 엣지를 쉽게 적용하기 위한 Modifier.
 *
 * 사용 예:
 * - val state = rememberLazyListState()
 * - LazyColumn(state = state, modifier = Modifier.verticalScrollFadingEdge(state))
 *
 * [ScrollFadingEdgeMode.PaddingReveal] (기본값)에서는 페이드 마스크가 항상 존재한다.
 * `contentPadding`(top/bottom)으로 여백을 확보하면, 컨텐츠가 그 아래로 스크롤되며
 * 페이드가 자연스럽게 "드러나는" 느낌이 된다(없다/생기다 방식이 아님).
 *
 * 권장:
 * - `contentPadding`의 top/bottom은 각각 `top`/`bottom` 이상으로 준다.
 */
enum class ScrollFadingEdgeMode {
    /**
     * 페이드 마스크를 항상 최대 강도로 적용한다.
     * `contentPadding`과 함께 쓰면 자연스럽다.
     */
    PaddingReveal,

    /**
     * 스크롤 오프셋 및 하단 도달 여부에 따라 페이드 강도를 0..1로 변화시킨다.
     */
    ScrollStrength,
}

fun Modifier.verticalScrollFadingEdge(
    state: LazyListState,
    top: Dp = 10.dp,
    bottom: Dp = 10.dp,
    mode: ScrollFadingEdgeMode = ScrollFadingEdgeMode.PaddingReveal,
): Modifier = composed {
    if (mode == ScrollFadingEdgeMode.PaddingReveal) {
        return@composed this.verticalFadingEdge(
            top = top,
            bottom = bottom,
            topStrength = 1f,
            bottomStrength = 1f,
        )
    }

    val density = LocalDensity.current
    val topFadePx = with(density) { top.toPx() }.coerceAtLeast(1f)
    val bottomFadePx = with(density) { bottom.toPx() }.coerceAtLeast(1f)

    val topStrength by remember(state, topFadePx) {
        derivedStateOf {
            if (state.firstVisibleItemIndex > 0) return@derivedStateOf 1f
            (state.firstVisibleItemScrollOffset / topFadePx).coerceIn(0f, 1f)
        }
    }
    val bottomStrength by remember(state, bottomFadePx) {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val total = layoutInfo.totalItemsCount
            if (total <= 0) return@derivedStateOf 0f

            val last = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf 0f
            if (last.index < total - 1) return@derivedStateOf 1f

            val overflowPx = (last.offset + last.size) - layoutInfo.viewportEndOffset
            if (overflowPx <= 0) 0f else (overflowPx / bottomFadePx).coerceIn(0f, 1f)
        }
    }

    this.verticalFadingEdge(
        top = top,
        bottom = bottom,
        topStrength = topStrength,
        bottomStrength = bottomStrength,
    )
}

/**
 * [LazyGridState] 기반 스크롤 영역에 상/하단 페이딩 엣지를 쉽게 적용하기 위한 Modifier.
 *
 * 사용 예:
 * - val state = rememberLazyGridState()
 * - LazyVerticalGrid(state = state, modifier = Modifier.verticalScrollFadingEdge(state))
 */
fun Modifier.verticalScrollFadingEdge(
    state: LazyGridState,
    top: Dp = 10.dp,
    bottom: Dp = 10.dp,
    mode: ScrollFadingEdgeMode = ScrollFadingEdgeMode.PaddingReveal,
): Modifier = composed {
    if (mode == ScrollFadingEdgeMode.PaddingReveal) {
        return@composed this.verticalFadingEdge(
            top = top,
            bottom = bottom,
            topStrength = 1f,
            bottomStrength = 1f,
        )
    }

    val density = LocalDensity.current
    val topFadePx = with(density) { top.toPx() }.coerceAtLeast(1f)
    val bottomFadePx = with(density) { bottom.toPx() }.coerceAtLeast(1f)

    val topStrength by remember(state, topFadePx) {
        derivedStateOf {
            if (state.firstVisibleItemIndex > 0) return@derivedStateOf 1f
            (state.firstVisibleItemScrollOffset / topFadePx).coerceIn(0f, 1f)
        }
    }
    val bottomStrength by remember(state, bottomFadePx) {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val total = layoutInfo.totalItemsCount
            if (total <= 0) return@derivedStateOf 0f

            val last = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf 0f
            if (last.index < total - 1) return@derivedStateOf 1f

            val overflowPx = (last.offset.y + last.size.height) - layoutInfo.viewportEndOffset
            if (overflowPx <= 0) 0f else (overflowPx / bottomFadePx).coerceIn(0f, 1f)
        }
    }

    this.verticalFadingEdge(
        top = top,
        bottom = bottom,
        topStrength = topStrength,
        bottomStrength = bottomStrength,
    )
}
