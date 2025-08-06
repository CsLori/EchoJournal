package com.cslori.echojournal.echos.presentation.echos

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cslori.echojournal.R
import com.cslori.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.cslori.echojournal.core.presentation.designsystem.theme.bgGradient
import com.cslori.echojournal.core.util.ObserveAsEvents
import com.cslori.echojournal.core.util.isAppInForeground
import com.cslori.echojournal.echos.domain.recording.RecordingDetails
import com.cslori.echojournal.echos.presentation.EchosEvent
import com.cslori.echojournal.echos.presentation.echos.components.EchoList
import com.cslori.echojournal.echos.presentation.echos.components.EchosEmptyBackground
import com.cslori.echojournal.echos.presentation.echos.components.EchosTopBar
import com.cslori.echojournal.echos.presentation.echos.components.FilterRow
import com.cslori.echojournal.echos.presentation.echos.components.QuickRecordFloatingActionButton
import com.cslori.echojournal.echos.presentation.echos.components.RecordingSheet
import com.cslori.echojournal.echos.presentation.echos.models.AudioCaptureMethod
import com.cslori.echojournal.echos.presentation.echos.models.RecordingState
import org.koin.androidx.compose.koinViewModel


@Composable
fun EchosRoot(
    onNavigateToCreateEcho: (RecordingDetails) -> Unit,
    viewModel: EchosViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasRecordAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] == true
        if (hasRecordAudioPermission && state.currentCaptureMethod == AudioCaptureMethod.STANDARD) {
            viewModel.onAction(EchosAction.AudioPermissionGranted)
        }
    }

    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EchosEvent.RequestAudioPermission -> {
                permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
            }

            is EchosEvent.DoneRecording -> {
                onNavigateToCreateEcho(event.details)
            }

            EchosEvent.RecordingTooShort -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.audio_recording_was_too_short),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val isAppInForeground by isAppInForeground()

    LaunchedEffect(isAppInForeground, state.recordingState) {
        if (!isAppInForeground && state.recordingState == RecordingState.NORMAL_CAPTURE) {
            viewModel.onAction(EchosAction.PauseRecordingClick)
        }
    }

    EchosScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun EchosScreen(
    state: EchosState,
    onAction: (EchosAction) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            EchosTopBar(onSettingsClick = { onAction(EchosAction.SettingsClick) })
        },
        floatingActionButton = {
            QuickRecordFloatingActionButton(
                onClick = { onAction(EchosAction.RecordFabClick) },
                isQuickRecording = state.recordingState == RecordingState.QUICK_CAPTURE,
                onLongPressStart = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                    if(hasPermission) {
                        onAction(EchosAction.RecordButtonLongClick)
                    } else {
                        onAction(EchosAction.RequestPermissionQuickRecording)
                    }
                },
                onLongPressEnd = { isCancelled ->
                    if (isCancelled) {
                        onAction(EchosAction.CancelRecordingClick)
                    } else {
                        onAction(EchosAction.CompleteRecordingClick)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = MaterialTheme.colorScheme.bgGradient
                )
                .padding(innerPadding)
        ) {
            FilterRow(
                moodChipContent = state.moodChipContent,
                hasActiveMoodFilters = state.hasActiveMoodFilters,
                selectedEchoFilterChip = state.selectedEchoFilterChip,
                moods = state.moods,
                topicChipTitle = state.topicChipTitle,
                hasActiveTopicFilters = state.hasActiveTopicFilters,
                topics = state.topics,
                onAction = onAction,
//                modifier = Modifier.fillMaxWidth()
            )
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .wrapContentSize(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                !state.hasEchosRecorded -> {
                    EchosEmptyBackground(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                else -> {
                    EchoList(
                        sections = state.echoDaySections,
                        onPlayClick = { echoId ->
                            onAction(EchosAction.PlayEchoClick(echoId))
                        },
                        onPauseClick = {
                            onAction(EchosAction.PauseAudioClick)
                        },
                        onTrackSizeAvailable = { trackSize ->
                            onAction(EchosAction.TrackSizeAvailable(trackSize))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
        }

        if (state.recordingState in listOf(
                RecordingState.NORMAL_CAPTURE, RecordingState.PAUSED
            )
        ) {
            RecordingSheet(
                formattedRecordDuration = state.formattedRecordDuration,
                isRecording = state.recordingState == RecordingState.NORMAL_CAPTURE,
                onDismiss = { onAction(EchosAction.CancelRecordingClick) },
                onPauseClick = { onAction(EchosAction.PauseRecordingClick) },
                onResumeClick = { onAction(EchosAction.ResumeRecordingClick) },
                onCompleteRecordingClick = { onAction(EchosAction.CompleteRecordingClick) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun EchosScreenPreview() {
    EchoJournalTheme {
        EchosScreen(
            state = EchosState(
                echos = emptyMap(),
                isLoading = false,
                hasEchosRecorded = false
            ),
            onAction = {}
        )
    }
}