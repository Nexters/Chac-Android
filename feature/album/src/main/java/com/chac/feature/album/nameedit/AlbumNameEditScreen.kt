package com.chac.feature.album.nameedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.chac.core.designsystem.ui.theme.ChacColors
import com.chac.feature.album.navigation.AlbumNameEditSource

@Composable
fun AlbumNameEditRoute(
    source: AlbumNameEditSource,
    selectedIds: LongArray,
    defaultAlbumName: String,
    onBack: () -> Unit,
    onSaveCompleted: (String, Int) -> Unit,
) {
    // TODO(#album-name-edit): Implement UI and save flow.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ChacColors.Background),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "AlbumNameEdit (todo) ids=${selectedIds.size} default=$defaultAlbumName")
    }
}

