package com.cslori.echojournal.echos.presentation.echos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cslori.echojournal.R
import com.cslori.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.cslori.echojournal.core.util.UiText
import com.cslori.echojournal.echos.presentation.EchosEvent
import com.cslori.echojournal.echos.presentation.echos.models.AudioCaptureMethod
import com.cslori.echojournal.echos.presentation.echos.models.EchoFilterChip
import com.cslori.echojournal.echos.presentation.echos.models.MoodChipContent
import com.cslori.echojournal.echos.presentation.models.MoodUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.List

class EchosViewModel : ViewModel() {
    private var hasLoadedInitialData = false

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())


    private val _state = MutableStateFlow(EchosState())

    private val _eventChannel = Channel<EchosEvent>()
    val events = _eventChannel.receiveAsFlow()
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeFilters()
                hasLoadedInitialData = true
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EchosState()
        )

    fun onAction(action: EchosAction) {
        when (action) {
            EchosAction.FabClick -> {
                requestAudioPermission()
                _state.update {
                    it.copy(
                        currentCaptureMethod = AudioCaptureMethod.STANDARD
                    )
                }
            }
            EchosAction.FabLongClick -> {
                requestAudioPermission()
                _state.update {
                    it.copy(
                        currentCaptureMethod = AudioCaptureMethod.QUICK
                    )
                }
            }
            EchosAction.MoodChipClick -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = EchoFilterChip.MOODS
                    )
                }
            }

            is EchosAction.RemoveFilters -> {
                when (action.filterType) {
                    EchoFilterChip.MOODS -> selectedMoodFilters.update { emptyList() }
                    EchoFilterChip.TOPICS -> selectedTopicFilters.update { emptyList() }
                }
            }

            EchosAction.TopicChipClick -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = EchoFilterChip.TOPICS
                    )
                }
            }

            EchosAction.SettingsClick -> {}
            EchosAction.DismissTopicDropdown,
            EchosAction.DismissMoodDropdown -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = null
                    )
                }
            }

            is EchosAction.FilterByMood -> {
                toggleMoodFilter(action.moodUi)
            }

            is EchosAction.FilterByTopic -> {
                toggleTopicFilter(action.topic)
            }

            EchosAction.PauseClick -> {}
            is EchosAction.PlayEchoClick -> {}
            is EchosAction.TrackSizeAvailable -> {}
            EchosAction.AudioPermissionGranted -> {}
        }
    }

    private fun requestAudioPermission() = viewModelScope.launch {
        _eventChannel.send(EchosEvent.RequestAudioPermission)
    }


    private fun toggleMoodFilter(moodUi: MoodUi) {
        selectedMoodFilters.update { selectedMoods ->
            if (moodUi in selectedMoods) {
                selectedMoods - moodUi
            } else {
                selectedMoods + moodUi
            }
        }
    }

    private fun toggleTopicFilter(topic: String) {
        selectedTopicFilters.update { selectedTopics ->
            if (topic in selectedTopics) {
                selectedTopics - topic
            } else {
                selectedTopics + topic
            }
        }
    }

    private fun observeFilters() {
        combine(
            selectedMoodFilters,
            selectedTopicFilters
        ) { selectedMoods, selectedTopics ->
            _state.update {
                it.copy(
                    topics = it.topics.map { selectableTopic ->
                        Selectable(
                            item = selectableTopic.item,
                            selected = selectedTopics.contains(selectableTopic.item)
                        )
                    },
                    moods = MoodUi.entries.map { moodUi ->
                        Selectable(
                            item = moodUi,
                            selected = selectedMoods.contains(moodUi)
                        )
                    },
                    hasActiveMoodFilters = selectedMoods.isNotEmpty(),
                    hasActiveTopicFilters = selectedTopics.isNotEmpty(),
                    topicChipTitle = selectedTopics.deriveTopicsToText(),
                    moodChipContent = selectedMoods.asMoodChipContent()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun List<String>.deriveTopicsToText(): UiText {
        return when (size) {
            0 -> UiText.StringResource(R.string.all_topics)
            1 -> UiText.Dynamic(this.first())
            2 -> UiText.Dynamic("${this.first()} ${this.last()}")
            else -> {
                val extraElement = size - 2
                UiText.Dynamic("${this.first()}, ${this[1]} +$extraElement")
            }
        }
    }

    private fun List<MoodUi>.asMoodChipContent(): MoodChipContent {
        if (this.isEmpty()) return MoodChipContent()
        val icons = this.map { it.iconSet.fill }
        val moodNames = this.map { it.title }

        return when (size) {
            1 -> MoodChipContent(
                iconsRes = icons,
                title = moodNames.first()
            )

            2 -> MoodChipContent(
                iconsRes = icons,
                title = UiText.Combined(
                    format =
                        "%s, %s",
                    uiTexts = moodNames.toTypedArray()
                )
            )

            else -> {
                val extraElementCount = size - 2
                MoodChipContent(
                    iconsRes = icons,
                    title = UiText.Combined(
                        format =
                            "%s, %s +$extraElementCount",
                        uiTexts = moodNames.take(2).toTypedArray()
                    )
                )
            }
        }
    }
}