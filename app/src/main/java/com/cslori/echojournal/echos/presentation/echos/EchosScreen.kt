package com.cslori.echojournal.echos.presentation.echos

import android.Manifest
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cslori.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cslori.echojournal.core.presentation.designsystem.theme.bgGradient
import com.cslori.echojournal.core.util.ObserveAsEvents
import com.cslori.echojournal.echos.presentation.EchosEvent
import com.cslori.echojournal.echos.presentation.echos.components.EchoList
import com.cslori.echojournal.echos.presentation.echos.components.EchoRecordFab
import com.cslori.echojournal.echos.presentation.echos.components.EchosEmptyBackground
import com.cslori.echojournal.echos.presentation.echos.components.EchosTopBar
import com.cslori.echojournal.echos.presentation.echos.components.FilterRow
import com.cslori.echojournal.echos.presentation.echos.models.AudioCaptureMethod


@Composable
fun EchosRoot(
    viewModel: EchosViewModel = viewModel()
//            viewModel: EchosViewModel = koinViewModel()
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

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EchosEvent.RequestAudioPermission -> {
                permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
            }
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
    Scaffold(
        topBar = {
            EchosTopBar(onSettingsClick = { onAction(EchosAction.SettingsClick) })
        },
        floatingActionButton = {
            EchoRecordFab(
                onClick = { onAction(EchosAction.FabClick) }
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
                            onAction(EchosAction.PauseClick)
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