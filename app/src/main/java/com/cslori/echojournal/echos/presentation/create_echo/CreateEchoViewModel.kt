package com.cslori.echojournal.echos.presentation.create_echo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cslori.echojournal.app.navigation.NavigationRoute
import com.cslori.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import com.cslori.echojournal.echos.domain.audio.AudioPlayer
import com.cslori.echojournal.echos.domain.echo.Echo
import com.cslori.echojournal.echos.domain.echo.EchoDataSource
import com.cslori.echojournal.echos.domain.echo.Mood
import com.cslori.echojournal.echos.domain.recording.RecordingStorage
import com.cslori.echojournal.echos.domain.settings.SettingsPreferences
import com.cslori.echojournal.echos.presentation.echos.models.PlaybackState
import com.cslori.echojournal.echos.presentation.echos.models.TrackSizeInfo
import com.cslori.echojournal.echos.presentation.models.MoodUi
import com.cslori.echojournal.echos.presentation.util.AmplitudeNormalizer
import com.cslori.echojournal.echos.presentation.util.toRecordingDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import kotlin.time.Duration

open class CreateEchoViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val recordingStorage: RecordingStorage,
    private val audioPlayer: AudioPlayer,
    private val echoDataSource: EchoDataSource,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val route = savedStateHandle.toRoute<NavigationRoute.CreateEcho>()
    private val recordingDetails = route.toRecordingDetails()

    private var eventChannel = Channel<CreateEchoEvent>()
    val events = eventChannel.receiveAsFlow()
    private var hasLoadedInitialData = false
    private val restoredTopics = savedStateHandle.get<String>("topics")?.split(",")
    private var _state = MutableStateFlow(
        CreateEchoState(
            playbackTotalDuration = recordingDetails.duration,
            titleText = savedStateHandle["titleText"] ?: "",
            noteText = savedStateHandle["noteText"] ?: "",
            topics = restoredTopics ?: emptyList(),
            mood = savedStateHandle.get<String>("mood")?.let { MoodUi.valueOf(it) },
            showMoodSelector = savedStateHandle.get<String>("mood") == null,
            canSaveEcho = savedStateHandle.get<Boolean>("canSaveEcho") == true
        )
    )
    val state = _state.onStart {
        if (!hasLoadedInitialData) {
            observeAddTopicText()
            fetchDefaultSettings()

            hasLoadedInitialData = true
        }
    }.onEach { state ->
        savedStateHandle["titleText"] = state.titleText
        savedStateHandle["noteText"] = state.noteText
        savedStateHandle["topics"] = state.topics.joinToString(",")
        savedStateHandle["mood"] = state.mood?.name
        savedStateHandle["canSaveEcho"] = state.canSaveEcho

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreateEchoState())

    private var durationJob: Job? = null

    fun onAction(action: CreateEchoAction) {
        when (action) {
            CreateEchoAction.ConfirmMood -> onConfirmMood()
            CreateEchoAction.DismissMoodSelector -> onDismissMoodSelector()
            CreateEchoAction.DismissTopiSuggestions -> onDismissTopicSuggestions()
            is CreateEchoAction.MoodClick -> onMoodClick(action.mood)
            is CreateEchoAction.NoteTextChange -> onNoteTextChange(action.text)
            CreateEchoAction.PauseAudioClick -> audioPlayer.pause()
            CreateEchoAction.PlayAudioClick -> onPlayAudioClick()
            is CreateEchoAction.RemoveTopicClick -> onRemoveTopicClick(action.topic)
            CreateEchoAction.SaveClick -> onSaveClick()
            CreateEchoAction.SelectMoodClick -> onSelectMoodClick()
            is CreateEchoAction.TitleTextChange -> onTitleTextChange(action.text)
            is CreateEchoAction.TopicClick -> onTopicClick(action.topic)
            is CreateEchoAction.TopicTextChange -> onAddTopicTextChange(action.text)
            is CreateEchoAction.TrackSizeAvailable -> onTrackSizeAvailable(action.trackSizeInfo)
            CreateEchoAction.DismissConfirmLeaveDialog -> onDismissConfirmLeaveDialog()
            CreateEchoAction.CancelClick,
            CreateEchoAction.NavigateBackClick,
            CreateEchoAction.GoBack -> onShowConfirmLeaveDialog()
        }
    }

    private fun fetchDefaultSettings() {
        settingsPreferences
            .observeDefaultMood()
            .take(1)
            .onEach { defaultMood ->
                val moodUi = MoodUi.valueOf(defaultMood.name)
                _state.update {
                    it.copy(
                        selectedMood = moodUi,
                        mood = moodUi,
                        showMoodSelector = false
                    )
                }
            }.launchIn(viewModelScope)

        settingsPreferences
            .observeDefaultTopics()
            .take(1)
            .onEach { defaultTopics ->
                _state.update {
                    it.copy(
                        topics = defaultTopics
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun onNoteTextChange(text: String) {
        _state.update {
            it.copy(
                noteText = text,
            )
        }
    }

    private fun onPlayAudioClick() {
        if (state.value.playBackState == PlaybackState.PAUSED) {
            audioPlayer.resume()
        } else {
            Timber.d("DDD - play pressed")
            audioPlayer.play(
                recordingDetails.filePath ?: throw IllegalStateException("File path can't be null"),
                onComplete = {
                    _state.update {
                        it.copy(
                            playBackState = PlaybackState.STOPPED,
                            durationPlayed = Duration.ZERO
                        )
                    }
                }
            )
            durationJob = audioPlayer.activeTrack.filterNotNull().onEach { track ->
                _state.update {
                    it.copy(
                        playBackState = if (track.isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED,
                        durationPlayed = track.durationPlayed
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeAddTopicText() {
        state.map { it.addTopicText }.distinctUntilChanged().debounce(300).onEach { query ->
            _state.update {
                it.copy(
                    showTopicSuggestions = query.isNotBlank() && query.trim() !in it.topics,
                    searchResults = listOf(
                        "hello",
                        "helloworld"
                    ).asUnselectedItems()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun onTrackSizeAvailable(trackSizeInfo: TrackSizeInfo) {
        viewModelScope.launch(Dispatchers.Default) {
            val finalAmplitudes = AmplitudeNormalizer.normalize(
                sourceAmplitudes = recordingDetails.amplitudes,
                trackWidth = trackSizeInfo.trackWidth,
                barWidth = trackSizeInfo.barWidth,
                spacing = trackSizeInfo.spacing
            )
            _state.update {
                it.copy(
                    playbackAmplitudes = finalAmplitudes
                )
            }
        }
    }


    private fun onDismissConfirmLeaveDialog() {
        _state.update {
            it.copy(
                showConfirmLeaveDialog = false
            )
        }
    }

    private fun onSaveClick() {
        if (recordingDetails.filePath == null || !state.value.canSaveEcho) {
            return
        }
        viewModelScope.launch {
            val savedFilePath = recordingStorage.savePersistently(
                tempFilePath = recordingDetails.filePath
            )

            if (savedFilePath == null) {
                eventChannel.send(CreateEchoEvent.FailedToSaveFile)
                return@launch
            }
            val currentState = state.value
            val echo = Echo(
                mood = currentState.mood?.let { Mood.valueOf(it.name) }
                    ?: throw java.lang.IllegalStateException("Mood must be set before saving Echo"),
                title = currentState.titleText.trim(),
                note = currentState.noteText.ifBlank { null },
                topics = currentState.topics,
                audioFilePath = savedFilePath,
                audioPlaybackLength = currentState.playbackTotalDuration,
                audioAmplitudes = recordingDetails.amplitudes,
                recordedAt = Instant.now()
            )
            echoDataSource.insertEcho(echo)
            eventChannel.send(CreateEchoEvent.EchoSuccessfullySaved)
        }
    }


    private fun onShowConfirmLeaveDialog() {
        _state.update {
            it.copy(
                showConfirmLeaveDialog = true
            )
        }
    }

    private fun onDismissTopicSuggestions() {
        _state.update {
            it.copy(
                showTopicSuggestions = false,
            )
        }
    }

    private fun onRemoveTopicClick(topic: String) {
        _state.update {
            it.copy(
                topics = it.topics - topic,
            )
        }
    }

    private fun onTopicClick(topic: String) {
        _state.update {
            it.copy(
                addTopicText = "",
                topics = (it.topics + topic).distinct(),
            )
        }
    }

    private fun onTitleTextChange(text: String) {
        _state.update {
            it.copy(
                titleText = text,
                canSaveEcho = text.isNotBlank() && it.mood != null,
            )
        }
    }

    private fun onAddTopicTextChange(text: String) {
        _state.update {
            it.copy(
                addTopicText = text.filter { char ->
                    char.isLetterOrDigit()
                },
            )
        }
    }

    private fun onSelectMoodClick() {
        _state.update {
            it.copy(
                showMoodSelector = true,
            )
        }
    }

    private fun onMoodClick(moodUi: MoodUi) {
        _state.update {
            it.copy(
                selectedMood = moodUi,
            )
        }
    }

    private fun onDismissMoodSelector() {
        _state.update {
            it.copy(
                showMoodSelector = false,
            )
        }
    }

    private fun onConfirmMood() {
        _state.update {
            it.copy(
                mood = it.selectedMood,
                canSaveEcho = it.titleText.isNotBlank(),
                showMoodSelector = false,
            )
        }
    }
}