package com.chac.feature.album.nameedit

import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chac.core.designsystem.ui.component.ChacImage
import com.chac.core.designsystem.ui.icon.Back
import com.chac.core.designsystem.ui.icon.ChacIcons
import com.chac.core.designsystem.ui.icon.Close
import com.chac.core.designsystem.ui.theme.ChacColors
import com.chac.core.designsystem.ui.theme.ChacTextStyles
import com.chac.core.designsystem.ui.theme.ChacTheme
import com.chac.core.permission.compose.rememberWriteRequestLauncher
import com.chac.core.resources.R
import com.chac.feature.album.navigation.AlbumNameEditSource

@Composable
fun AlbumNameEditRoute(
    source: AlbumNameEditSource,
    selectedIds: LongArray,
    defaultAlbumName: String,
    onBack: () -> Unit,
    onSaveCompleted: (String, Int) -> Unit,
    viewModel: AlbumNameEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val writeRequestLauncher = rememberWriteRequestLauncher(
        onGranted = { viewModel.save() },
    )

    LaunchedEffect(viewModel, source, defaultAlbumName, selectedIds) {
        viewModel.initialize(
            source = source,
            selectedIds = selectedIds,
            defaultAlbumName = defaultAlbumName,
        )

        viewModel.saveCompletedEvents.collect { event ->
            onSaveCompleted(event.title, event.savedCount)
        }
    }

    when (val state = uiState) {
        is AlbumNameEditUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ChacColors.Background),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = ChacColors.Primary)
            }
        }

        is AlbumNameEditUiState.Ready -> {
            AlbumNameEditScreen(
                thumbnailUriString = state.thumbnailUriString,
                albumName = state.albumName,
                selectedCount = state.selectedCount,
                onAlbumNameChanged = viewModel::setAlbumName,
                onClearAlbumName = viewModel::clearAlbumName,
                onClickBack = onBack,
                onClickSave = {
                    if (state.uriStrings.isEmpty()) return@AlbumNameEditScreen

                    val intentSender = MediaStore.createWriteRequest(
                        context.contentResolver,
                        state.uriStrings.map { it.toUri() },
                    ).intentSender
                    writeRequestLauncher(intentSender)
                },
                isSaving = false,
            )
        }

        is AlbumNameEditUiState.Saving -> {
            AlbumNameEditScreen(
                thumbnailUriString = state.thumbnailUriString,
                albumName = state.albumName,
                selectedCount = state.selectedCount,
                onAlbumNameChanged = {},
                onClearAlbumName = {},
                onClickBack = onBack,
                onClickSave = {},
                isSaving = true,
            )
        }

        is AlbumNameEditUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ChacColors.Background),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.message,
                    style = ChacTextStyles.Body,
                    color = ChacColors.Text03,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp),
                )
            }
            BackHandler(onBack = onBack)
        }
    }
}

@Composable
private fun AlbumNameEditScreen(
    thumbnailUriString: String?,
    albumName: String,
    selectedCount: Int,
    onAlbumNameChanged: (String) -> Unit,
    onClearAlbumName: () -> Unit,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit,
    isSaving: Boolean,
) {
    BackHandler(onBack = onClickBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChacColors.Background)
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
    ) {
        AlbumNameEditTopBar(onClickBack = onClickBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (thumbnailUriString != null) {
                ChacImage(
                    model = thumbnailUriString,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(28.dp)),
                )
            } else {
                Spacer(modifier = Modifier.size(120.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.album_name_edit_label),
                    style = ChacTextStyles.Caption,
                    color = ChacColors.Text04Caption,
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = albumName,
                    onValueChange = onAlbumNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        if (albumName.isNotBlank()) {
                            IconButton(
                                onClick = onClearAlbumName,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(ChacColors.Token00000040),
                            ) {
                                Icon(
                                    imageVector = ChacIcons.Close,
                                    contentDescription = null,
                                    tint = ChacColors.Text03,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ChacColors.BackgroundPopup,
                        unfocusedContainerColor = ChacColors.BackgroundPopup,
                        disabledContainerColor = ChacColors.BackgroundPopup,
                        focusedTextColor = ChacColors.Text01,
                        unfocusedTextColor = ChacColors.Text01,
                        focusedIndicatorColor = ChacColors.BackgroundPopup,
                        unfocusedIndicatorColor = ChacColors.BackgroundPopup,
                        cursorColor = ChacColors.Primary,
                    ),
                    textStyle = ChacTextStyles.SubTitle01,
                )
            }
        }

        Button(
            onClick = onClickSave,
            enabled = !isSaving && selectedCount > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ChacColors.Primary,
                contentColor = ChacColors.TextBtn01,
                disabledContainerColor = ChacColors.Disable,
                disabledContentColor = ChacColors.TextBtn03,
            ),
        ) {
            Text(
                text = stringResource(R.string.gallery_save_album_count, selectedCount),
                style = ChacTextStyles.Btn,
            )
        }
    }

    if (isSaving) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ChacColors.Token00000040),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = ChacColors.Primary)
        }
    }
}

@Composable
private fun AlbumNameEditTopBar(
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(
            onClick = onClickBack,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector = ChacIcons.Back,
                contentDescription = stringResource(R.string.gallery_back_cd),
                tint = ChacColors.Text01,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = stringResource(R.string.album_name_edit_title),
            style = ChacTextStyles.Title,
            color = ChacColors.Text01,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlbumNameEditScreenPreview() {
    ChacTheme {
        AlbumNameEditScreen(
            thumbnailUriString = "content://sample/0",
            albumName = "부산 광역시",
            selectedCount = 4,
            onAlbumNameChanged = {},
            onClearAlbumName = {},
            onClickBack = {},
            onClickSave = {},
            isSaving = false,
        )
    }
}
