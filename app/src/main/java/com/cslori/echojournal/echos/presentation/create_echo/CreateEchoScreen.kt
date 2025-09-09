package com.cslori.echojournal.echos.presentation.create_echo

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cslori.echojournal.R
import com.cslori.echojournal.core.presentation.designsystem.buttons.PrimaryButton
import com.cslori.echojournal.core.presentation.designsystem.buttons.SecondaryButton
import com.cslori.echojournal.core.presentation.designsystem.textfields.TransparentHintTextField
import com.cslori.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.cslori.echojournal.core.presentation.designsystem.theme.secondary70
import com.cslori.echojournal.core.presentation.designsystem.theme.secondary95
import com.cslori.echojournal.core.util.ObserveAsEvents
import com.cslori.echojournal.echos.presentation.components.MoodPlayer
import com.cslori.echojournal.echos.presentation.create_echo.components.SelectMoodSheet
import com.cslori.echojournal.echos.presentation.create_echo.components.TopicsRow
import com.cslori.echojournal.echos.presentation.models.MoodUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateEchoScreenRoot(
    onConfirmLeave: () -> Unit,
    viewModel: CreateEchoViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CreateEchoEvent.FailedToSaveFile -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.failed_to_save_file),
                    Toast.LENGTH_SHORT
                ).show()
                onConfirmLeave()
            }

            CreateEchoEvent.EchoSuccessfullySaved -> {
                onConfirmLeave()
            }
        }
    }
    CreateEchoScreen(
        state = state,
        onAction = viewModel::onAction,
        onConfirmLeave = onConfirmLeave
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEchoScreen(
    state: CreateEchoState,
    onAction: (CreateEchoAction) -> Unit,
    onConfirmLeave: () -> Unit
) {

    BackHandler(
        enabled = !state.showConfirmLeaveDialog
    ) {
        onAction(CreateEchoAction.GoBack)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.new_entry), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(CreateEchoAction.NavigateBackClick) },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }

                }

            )
        }
    ) { innerPadding ->
        val descriptionFocusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.mood == null) {
                    FilledIconButton(
                        onClick = { onAction(CreateEchoAction.SelectMoodClick) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary95,
                            contentColor = MaterialTheme.colorScheme.secondary70
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_mood)
                        )
                    }
                } else {
                    Image(
                        imageVector = ImageVector.vectorResource(state.mood.iconSet.fill),
                        contentDescription = state.mood.title.asString(),
                        modifier = Modifier
                            .height(32.dp)
                            .clickable {
                                onAction(CreateEchoAction.SelectMoodClick)
                            },
                        contentScale = ContentScale.FillHeight
                    )
                }
                TransparentHintTextField(
                    text = state.titleText,
                    onValueChange = { onAction(CreateEchoAction.TitleTextChange(it)) },
                    modifier = Modifier.weight(1f),
                    hintText = stringResource(R.string.add_title),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            descriptionFocusRequester.requestFocus()
                        }
                    )
                )
            }
            MoodPlayer(
                onPlayClick = { onAction(CreateEchoAction.PlayAudioClick) },
                onPauseClick = { onAction(CreateEchoAction.PauseAudioClick) },
                moodUi = state.mood,
                playBackState = state.playBackState,
                playerProgress = { state.durationPlayedRatio },
                durationPlayed = state.durationPlayed,
                totalPlaybackDuration = state.playbackTotalDuration,
                powerRatios = state.playbackAmplitudes,
                onTrackSizeAvailable = { onAction(CreateEchoAction.TrackSizeAvailable(it)) },
            )

            TopicsRow(
                topics = state.topics,
                addTopicText = state.addTopicText,
                showTopicSuggestions = state.showTopicSuggestions,
                showCreateTopicOption = state.showCreateTopicOption,
                searchResults = state.searchResults,
                onAddTopicTextChange = { onAction(CreateEchoAction.TopicTextChange(it)) },
                onRemoveTopicClick = { onAction(CreateEchoAction.RemoveTopicClick(it)) },
                onTopicClick = { onAction(CreateEchoAction.TopicClick(it)) },
                onDismissTopicSuggestions = { onAction(CreateEchoAction.DismissTopiSuggestions) },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = stringResource(R.string.add_description),
                    modifier = Modifier.size(16.dp)
                )

                TransparentHintTextField(
                    text = state.noteText,
                    onValueChange = { onAction(CreateEchoAction.NoteTextChange(it)) },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(descriptionFocusRequester),
                    hintText = stringResource(R.string.add_description),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.cancel),
                    onClick = { onAction(CreateEchoAction.CancelClick) },
                    modifier = Modifier.fillMaxHeight(),
                )
                PrimaryButton(
                    text = stringResource(R.string.save),
                    onClick = { onAction(CreateEchoAction.SaveClick) },
                    modifier = Modifier.weight(1f),
                    enabled = state.canSaveEcho,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
        if (state.showMoodSelector) {
            SelectMoodSheet(
                selectedMood = state.selectedMood,
                onMoodClick = { onAction(CreateEchoAction.MoodClick(it)) },
                onDismiss = { onAction(CreateEchoAction.DismissMoodSelector) },
                onConfirmClick = { onAction(CreateEchoAction.ConfirmMood) },
            )
        }
        if (state.showConfirmLeaveDialog) {
            AlertDialog(
                onDismissRequest = { onAction(CreateEchoAction.DismissConfirmLeaveDialog) },
                confirmButton = {
                    TextButton(
                        onClick = onConfirmLeave
                    ) {
                        Text(
                            text = stringResource(R.string.discard),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onAction(CreateEchoAction.DismissConfirmLeaveDialog) }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.discard_recording),
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.this_cannot_be_undone),
                    )
                })
        }
    }
}

@Preview
@Composable
private fun CreateEchoScreenPreview() {
    EchoJournalTheme {
        CreateEchoScreen(
            state = CreateEchoState(
                mood = MoodUi.NEUTRAL,
                canSaveEcho = true
            ),
            onAction = {},
            onConfirmLeave = {}
        )
    }
}