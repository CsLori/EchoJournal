package com.cslori.echojournal.echos.presentation.settings

import com.cslori.echojournal.echos.presentation.models.MoodUi

data class SettingsState(
    val topics: List<String> = emptyList(),
    val selectedMood: MoodUi? = null,
    val searchText: String = "",
    val suggestedTopics: List<String> = emptyList(),
    val isTopicSuggestionsVisible: Boolean = false,
    val showCreateTopicOption: Boolean = false,
    val isTopicTextVisible: Boolean = false
)