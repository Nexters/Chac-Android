package com.chac.feature.album.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chac.core.designsystem.ui.theme.ChacColors
import com.chac.core.designsystem.ui.theme.ChacTextStyles
import com.chac.core.designsystem.ui.theme.ChacTheme
import com.chac.core.resources.R
import kotlinx.coroutines.launch

private const val ONBOARDING_PAGE_COUNT = 4
private const val CONTENT_PAGE_COUNT = 4

/**
 * 온보딩 화면 라우트
 *
 * @param onCompleted 온보딩 완료 콜백
 */
@Composable
fun OnboardingRoute(
    onCompleted: () -> Unit,
) {
    OnboardingScreen(onCompleted = onCompleted)
}

/**
 * 온보딩 화면
 *
 * @param onCompleted 온보딩 완료 콜백
 */
@Composable
private fun OnboardingScreen(
    onCompleted: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == ONBOARDING_PAGE_COUNT - 1

    Box {
        Image(
            painter = painterResource(R.drawable.im_onboarding_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.onboarding_skip),
                style = ChacTextStyles.Body,
                color = ChacColors.Text03,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(vertical = 16.dp)
                    .clickable(onClick = onCompleted),
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                if (page == 0) {
                    OnboardingFirstPage()
                } else {
                    OnboardingPage(page = page)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PageIndicator(
                currentPage = pagerState.currentPage,
                pageCount = CONTENT_PAGE_COUNT,
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isLastPage) {
                        onCompleted()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChacColors.Primary,
                    contentColor = ChacColors.TextBtn01,
                ),
            ) {
                Text(
                    text = if (isLastPage) {
                        stringResource(R.string.onboarding_start)
                    } else {
                        stringResource(R.string.onboarding_next)
                    },
                    style = ChacTextStyles.Btn,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * 온보딩 페이지 내용 (1~3페이지)
 *
 * @param page 페이지 인덱스
 */
@Composable
private fun OnboardingPage(
    page: Int,
    modifier: Modifier = Modifier,
) {
    val (titleRes, descriptionRes, imageRes) = when (page) {
        1 -> Triple(R.string.onboarding_page1_title, R.string.onboarding_page1_description, R.drawable.im_onboarding_pictogram1)
        2 -> Triple(R.string.onboarding_page2_title, R.string.onboarding_page2_description, R.drawable.im_onboarding_pictogram2)
        else -> Triple(R.string.onboarding_page3_title, R.string.onboarding_page3_description, R.drawable.im_onboarding_pictogram3)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(width = 140.dp, height = 120.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(titleRes),
            style = ChacTextStyles.Headline02,
            color = ChacColors.Text01,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(descriptionRes),
            style = ChacTextStyles.Body,
            color = ChacColors.Text03,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 온보딩 마지막 페이지 (4페이지) - "착" 브랜딩
 */
@Composable
private fun OnboardingFirstPage(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.im_onboarding_logo),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(26.dp))

        Image(
            painter = painterResource(R.drawable.im_onboarding_watermark),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.onboarding_page0_title),
            style = ChacTextStyles.Headline01,
            color = ChacColors.Text01,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 페이지 인디케이터
 *
 * @param currentPage 현재 페이지 인덱스
 * @param pageCount 표시할 인디케이터 수
 */
@Composable
private fun PageIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) {
                            ChacColors.Primary
                        } else {
                            ChacColors.Text04Caption.copy(alpha = 0.3f)
                        },
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    ChacTheme {
        OnboardingScreen(onCompleted = {})
    }
}
