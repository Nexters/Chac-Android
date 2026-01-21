package com.chac.feature.album.clustering.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chac.core.designsystem.ui.theme.ChacTheme
import com.chac.core.resources.R
import java.util.Locale

/**
 * 전체 사진 수를 요약 카드로 표시한다
 *
 * @param totalCount 전체 사진 개수
 */
@Composable
fun TotalPhotoSummary(
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.clustering_total_photo_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = formatCount(totalCount),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * 숫자 카운트를 로케일 기준 포맷 문자열로 변환한다
 *
 * @param count 포맷할 수량
 * @return 로케일 기준으로 포맷된 문자열
 */
internal fun formatCount(count: Int): String = String.format(Locale.getDefault(), "%,d", count)

@Preview(showBackground = true)
@Composable
private fun TotalPhotoSummaryPreview() {
    ChacTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TotalPhotoSummary(totalCount = 99_990)
        }
    }
}
