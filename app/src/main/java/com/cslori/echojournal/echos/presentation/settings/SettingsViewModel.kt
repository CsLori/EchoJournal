package com.cslori.echojournal.echos.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel : ViewModel() {

    private var hasLoadedInitialData = false
    private var _state = MutableStateFlow(SettingsState())
    val state = _state.onStart {
        if (!hasLoadedInitialData) {
            hasLoadedInitialData = true
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.AddButtonClick -> {}
            SettingsAction.BackClick -> {}
            SettingsAction.CreateTopicClick -> {}
            SettingsAction.DismissTopicDropdown -> {}
            is SettingsAction.MoodClick -> {}
            is SettingsAction.RemoveTopicClick -> {}
            is SettingsAction.SearchTextChange -> {}
        }
    }
}