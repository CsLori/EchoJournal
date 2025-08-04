package com.cslori.echojournal.echos.presentation.echos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cslori.echojournal.R
import com.cslori.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.cslori.echojournal.core.presentation.designsystem.theme.Microphone
import com.cslori.echojournal.core.presentation.designsystem.theme.Pause


private const val PRIMARY_BUTTON_BUBBLE_SIZE_DP = 128
private const val SECONDARY_BUTTON_BUBBLE_SIZE_DP = 48

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingSheet(
    formattedRecordDuration: String,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onCompleteRecordingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        SheetContent(
            formattedRecordDuration = formattedRecordDuration,
            isRecording = isRecording,
            onDismiss = onDismiss,
            onPauseClick = onPauseClick,
            onPlayClick = onResumeClick,
            onCompleteRecordingClick = onCompleteRecordingClick,
            modifier = modifier
        )
    }

}

@Composable
fun SheetContent(
    formattedRecordDuration: String,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
    onCompleteRecordingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryButtonSize = PRIMARY_BUTTON_BUBBLE_SIZE_DP.dp
    val secondaryButtonSize = SECONDARY_BUTTON_BUBBLE_SIZE_DP.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isRecording) {
                    stringResource(com.cslori.echojournal.R.string.recording_your_memories)
                } else {
                    stringResource(com.cslori.echojournal.R.string.recording_paused)
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = formattedRecordDuration,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFeatureSettings = "tnum",
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledIconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(secondaryButtonSize),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cancel),
                    )
                }

                BubbleFloatingActionButton(
                    showBubble = isRecording,
                    onClick = if (isRecording) {
                        onCompleteRecordingClick
                    } else {
                        onPlayClick
                    },
                    modifier = Modifier.size(primaryButtonSize),
                    icon = {
                        Icon(
                            imageVector = if (isRecording) {
                                Icons.Default.Check
                            } else Icons.Filled.Microphone,
                            contentDescription = if (isRecording) {
                                stringResource(R.string.finish_recording)
                            } else {
                                stringResource(R.string.resume_recording)
                            },
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                )

                FilledIconButton(
                    onClick = if (isRecording) {
                        onPauseClick
                    } else {
                        onCompleteRecordingClick
                    },
                    modifier = Modifier.size(secondaryButtonSize),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isRecording) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.Check
                        },
                        contentDescription = if (isRecording) {
                            stringResource(R.string.paused)
                        } else {
                            stringResource(R.string.finish_recording)
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecordingSheetPreview() {
    EchoJournalTheme {
        SheetContent(
            formattedRecordDuration = "00:10:34",
            isRecording = true,
            onDismiss = {},
            onPauseClick = {},
            onPlayClick = {},
            onCompleteRecordingClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}