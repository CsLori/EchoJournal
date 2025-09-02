package com.cslori.echojournal.echos.presentation.create_echo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cslori.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import com.cslori.echojournal.echos.presentation.models.MoodUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CreateEchoViewModel : ViewModel() {

    private var hasLoadedInitialData = false
    private var _state = MutableStateFlow(CreateEchoState())
    val state = _state.onStart {
        if (!hasLoadedInitialData) {
            observeAddTopicText()

            hasLoadedInitialData = true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreateEchoState())

    fun onAction(action: CreateEchoAction) {
        when (action) {
            CreateEchoAction.ConfirmMood -> onConfirmMood()
            CreateEchoAction.DismissMoodSelector -> onDismissMoodSelector()
            CreateEchoAction.DismissTopiSuggestions -> onDismissTopicSuggestions()
            is CreateEchoAction.MoodClick -> onMoodClick(action.mood)
            is CreateEchoAction.NotesTextChange -> TODO()
            CreateEchoAction.PauseAudioClick -> TODO()
            CreateEchoAction.PlayAudioClick -> TODO()
            is CreateEchoAction.RemoveTopicClick -> onRemoveTopicClick(action.topic)
            CreateEchoAction.SaveClick -> TODO()
            CreateEchoAction.SelectMoodClick -> onSelectMoodClick()
            is CreateEchoAction.TitleTextChange -> onTitleTextChange(action.text)
            is CreateEchoAction.TopicClick -> onTopicClick(action.topic)
            is CreateEchoAction.TopicTextChange -> onAddTopicTextChange(action.text)
            is CreateEchoAction.TrackSizeAvailable -> TODO()
            CreateEchoAction.DismissConfirmLeaveDialog -> onDismissConfirmLeaveDialog()
            CreateEchoAction.CancelClick,
            CreateEchoAction.NavigateBackClick,
            CreateEchoAction.GoBack -> onShowConfirmLeaveDialog()
        }
    }

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

    private fun onDismissConfirmLeaveDialog() {
        _state.update {
            it.copy(
                showConfirmLeaveDialog = false
            )
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
                canSaveEcho = text.isNotBlank() && it.mood != null
            )
        }
    }

    private fun onAddTopicTextChange(text: String) {
        _state.update {
            it.copy(
                addTopicText = text.filter {
                    it.isLetterOrDigit()
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