package com.chac.core.designsystem.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 알파 마스크를 이용해 컴포저블 컨텐츠에 상/하단 페이딩 엣지를 적용한다.
 *
 * 참고:
 * - Offscreen compositing + BlendMode.DstIn 으로 알파를 마스킹한다. 페이드 구간은 배경이 비쳐 보인다.
 * - 스크롤 상태 기반으로 strength(0..1)를 넘기면 페이드가 자연스럽게 변한다.
 */
fun Modifier.verticalFadingEdge(
    top: Dp = 24.dp,
    bottom: Dp = 24.dp,
    topStrength: Float = 1f, // 0f = 페이드 없음, 1f = 최대 페이드
    bottomStrength: Float = 1f, // 0f = 페이드 없음, 1f = 최대 페이드
): Modifier {
    val topS = topStrength.coerceIn(0f, 1f)
    val bottomS = bottomStrength.coerceIn(0f, 1f)
    if (topS == 0f && bottomS == 0f) return this

    return this
        // BlendMode.DstIn 마스킹이 정상 동작하도록 offscreen으로 그린다.
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()

            val h = size.height
            if (h <= 0f) return@drawWithContent

            val topPx = top.toPx().coerceAtLeast(0f)
            val bottomPx = bottom.toPx().coerceAtLeast(0f)

            // 픽셀을 비율로 변환하고 stop 순서가 꼬이지 않도록 보정한다.
            val topStop = (topPx / h).coerceIn(0f, 1f)
            val bottomStart = ((h - bottomPx) / h).coerceIn(0f, 1f)
            val midTop = minOf(topStop, bottomStart)
            val midBottom = maxOf(topStop, bottomStart)

            // DstIn은 소스 알파를 마스크로 사용한다 (0 = 완전 투명/클립, 1 = 완전 표시).
            val startMask = Color.Black.copy(alpha = 1f - topS)
            val endMask = Color.Black.copy(alpha = 1f - bottomS)
            val opaque = Color.Black // alpha = 1

            val brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0f to startMask,
                    midTop to opaque,
                    midBottom to opaque,
                    1f to endMask,
                ),
            )

            // 기존 컨텐츠를 소스 알파로 마스킹한다.
            drawRect(
                brush = brush,
                blendMode = BlendMode.DstIn,
            )
        }
}

fun Modifier.verticalFadingEdge(
    top: Dp = 24.dp,
    bottom: Dp = 24.dp,
    showTop: Boolean = true,
    showBottom: Boolean = true,
): Modifier = verticalFadingEdge(
    top = top,
    bottom = bottom,
    topStrength = if (showTop) 1f else 0f,
    bottomStrength = if (showBottom) 1f else 0f,
)
