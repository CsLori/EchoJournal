package com.cslori.echojournal.echos.presentation.echos

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
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cslori.echojournal.core.presentation.designsystem.theme.bgGradient
import com.cslori.echojournal.echos.presentation.echos.components.EchoRecordFab
import com.cslori.echojournal.echos.presentation.echos.components.EchosEmptyBackground
import com.cslori.echojournal.echos.presentation.echos.components.EchosTopBar
import com.cslori.echojournal.echos.presentation.echos.components.FilterRow


@Composable
fun EchosRoot(
    viewModel: EchosViewModel = viewModel()
//            viewModel: EchosViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
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
                isLoading = false,
                hasEchosRecorded = false
            ),
            onAction = {}
        )
    }
}