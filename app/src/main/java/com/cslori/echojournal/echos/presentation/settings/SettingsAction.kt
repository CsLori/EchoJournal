package com.cslori.echojournal.echos.presentation.settings

import com.cslori.echojournal.echos.presentation.models.MoodUi

sealed interface SettingsAction {
    data class SearchTextChange(val searchText: String) : SettingsAction
    data object CreateTopicClick : SettingsAction
    data class RemoveTopicClick(val topic: String) : SettingsAction
    data object BackClick : SettingsAction
    data object DismissTopicDropdown : SettingsAction
    data object AddButtonClick : SettingsAction
    data class MoodClick(val mood: MoodUi) : SettingsAction

}