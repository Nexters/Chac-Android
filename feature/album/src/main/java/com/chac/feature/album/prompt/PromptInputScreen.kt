package com.chac.feature.album.prompt

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chac.core.designsystem.ui.icon.ArrowTopRight
import com.chac.core.designsystem.ui.icon.ChacIcons
import com.chac.core.designsystem.ui.theme.ChacColors
import com.chac.core.designsystem.ui.theme.ChacTextStyles
import com.chac.core.resources.R

@Composable
fun PromptInputRoute(
    onSearchPrompt: (String) -> Unit,
    onClickLocationClustering: () -> Unit,
    onClickSettings: () -> Unit,
) {
    PromptInputScreen(
        onSearchPrompt = onSearchPrompt,
        onClickLocationClustering = onClickLocationClustering,
        onClickSettings = onClickSettings,
    )
}

@Composable
private fun PromptInputScreen(
    onSearchPrompt: (String) -> Unit,
    onClickLocationClustering: () -> Unit,
    onClickSettings: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var query by rememberSaveable { mutableStateOf("") }
    val trimmedQuery = query.trim()
    val canSearch = trimmedQuery.isNotEmpty()
    val recommendation1 = stringResource(R.string.prompt_recommendation_1)
    val recommendation2 = stringResource(R.string.prompt_recommendation_2)
    val recommendation3 = stringResource(R.string.prompt_recommendation_3)

    fun submitQuery() {
        if (!canSearch) return
        focusManager.clearFocus()
        onSearchPrompt(trimmedQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChacColors.Background)
            .padding(horizontal = 20.dp),
    ) {
        PromptTopBar(onClickSettings = onClickSettings)

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_album_section_header),
                contentDescription = null,
                tint = ChacColors.Text02,
                modifier = Modifier.size(36.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.prompt_title),
                style = ChacTextStyles.Headline01,
                color = ChacColors.Text01,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.prompt_recommendation_title),
                style = ChacTextStyles.Caption,
                color = ChacColors.Text04Caption,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PromptSuggestionChip(
                    text = recommendation1,
                    onClick = { query = recommendation1 },
                )
                PromptSuggestionChip(
                    text = recommendation2,
                    onClick = { query = recommendation2 },
                )
                PromptSuggestionChip(
                    text = recommendation3,
                    onClick = { query = recommendation3 },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onClickLocationClustering,
                shape = RoundedCornerShape(21.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = ChacColors.Stroke02,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ChacColors.Text02,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.prompt_location_classification_action),
                    style = ChacTextStyles.SubTitle03,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PromptInputBar(
            query = query,
            canSubmit = canSearch,
            onQueryChange = { query = it },
            onSubmit = ::submitQuery,
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PromptTopBar(
    onClickSettings: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.prompt_top_bar_title),
            style = ChacTextStyles.Title,
            color = ChacColors.Text01,
        )

        IconButton(
            onClick = onClickSettings,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.prompt_settings_cd),
                tint = ChacColors.Text04Caption,
            )
        }
    }
}

@Composable
private fun PromptSuggestionChip(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = ChacColors.Ffffff40,
                shape = RoundedCornerShape(21.dp),
            )
            .background(
                color = ChacColors.Ffffff5,
                shape = RoundedCornerShape(21.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = text,
            style = ChacTextStyles.Body.copy(fontSize = 14.sp, lineHeight = 18.sp),
            color = ChacColors.Text02,
            maxLines = 1,
        )
    }
}

@Composable
private fun PromptInputBar(
    query: String,
    canSubmit: Boolean,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 1.dp,
                    color = ChacColors.Ffffff5,
                    shape = RoundedCornerShape(21.dp),
                )
                .background(
                    color = ChacColors.Ffffff5,
                    shape = RoundedCornerShape(21.dp),
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = ChacTextStyles.Body.copy(
                    color = ChacColors.Text01,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                ),
                cursorBrush = SolidColor(ChacColors.Primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (query.isBlank()) {
                        Text(
                            text = stringResource(R.string.prompt_hint),
                            style = ChacTextStyles.Body.copy(fontSize = 14.sp, lineHeight = 18.sp),
                            color = ChacColors.Text04Caption,
                        )
                    }
                    innerTextField()
                },
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = if (canSubmit) ChacColors.Primary else ChacColors.Disable,
                    shape = CircleShape,
                )
                .clickable(
                    enabled = canSubmit,
                    onClick = onSubmit,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ChacIcons.ArrowTopRight,
                contentDescription = stringResource(R.string.prompt_search_action),
                tint = ChacColors.TextBtn01,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
