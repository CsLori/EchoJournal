package com.cslori.echojournal.echos.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cslori.echojournal.echos.domain.echo.Mood
import com.cslori.echojournal.echos.domain.settings.SettingsPreferences
import com.cslori.echojournal.echos.presentation.models.MoodUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var _state = MutableStateFlow(SettingsState())
    val state = _state.onStart {
        if (!hasLoadedInitialData) {
            observeSettings()
            hasLoadedInitialData = true
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )


    private fun observeSettings() {
        combine(
            settingsPreferences.observeDefaultTopics(),
            settingsPreferences.observeDefaultMood(),
        ) { topics, mood ->
            _state.value = SettingsState(
                topics = topics,
                selectedMood = MoodUi.valueOf(mood.name)
            )

        }.launchIn(viewModelScope)
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.AddButtonClick -> {}
            is SettingsAction.SelectTopicClick -> onSelectTopicClick(action.topic)
            SettingsAction.DismissTopicDropdown -> {}
            is SettingsAction.MoodClick -> onMoodClick(action.mood)
            is SettingsAction.RemoveTopicClick -> onRemoveTopicClick(action.topic)
            is SettingsAction.SearchTextChange -> {}
            else -> Unit
        }
    }

    private fun onRemoveTopicClick(topic: String) {
        viewModelScope.launch {
            val newDefaultTopics = (state.value.topics - topic).distinct()
            settingsPreferences.saveDefaultTopics(newDefaultTopics)
        }
    }

    private fun onSelectTopicClick(topic: String) {
        viewModelScope.launch {
            val newDefaultTopics = (state.value.topics + topic).distinct()
            settingsPreferences.saveDefaultTopics(newDefaultTopics)
        }
    }

    private fun onMoodClick(mood: MoodUi) {
        viewModelScope.launch {
            settingsPreferences.saveDefaultMood(Mood.valueOf(mood.name))
        }
    }
}