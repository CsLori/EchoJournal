package com.cslori.echojournal.echos.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cslori.echojournal.echos.domain.echo.EchoDataSource
import com.cslori.echojournal.echos.domain.echo.Mood
import com.cslori.echojournal.echos.domain.settings.SettingsPreferences
import com.cslori.echojournal.echos.presentation.models.MoodUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences,
    private val echoDataSource: EchoDataSource
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var _state = MutableStateFlow(SettingsState())
    val state = _state.onStart {
        if (!hasLoadedInitialData) {
            observeSettings()
            observeTopicSearchResults()
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

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeTopicSearchResults() {
        state
            .distinctUntilChangedBy { it.searchText }
            .map { it.searchText }
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isNotBlank()) {
                    echoDataSource.searchTopics(query)
                } else emptyFlow()
            }
            .onEach { filteredResults ->
                _state.update {
                    val filteredNonDefaultResults = filteredResults - it.topics
                    val searchText = it.searchText.trim()
                    val isNewTopic =
                        searchText !in filteredNonDefaultResults && searchText !in it.topics && searchText.isNotBlank()
                    it.copy(
                        isTopicSuggestionsVisible = filteredResults.isNotEmpty() || isNewTopic,
                        suggestedTopics = filteredNonDefaultResults,
                        showCreateTopicOption = isNewTopic
                    )
                }
            }.launchIn(viewModelScope)
    }
    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.AddButtonClick -> onAddButtonClick()
            is SettingsAction.SelectTopicClick -> onSelectTopicClick(action.topic)
            SettingsAction.DismissTopicDropdown -> onDismissTopicDropDown()
            is SettingsAction.MoodClick -> onMoodClick(action.mood)
            is SettingsAction.RemoveTopicClick -> onRemoveTopicClick(action.topic)
            is SettingsAction.SearchTextChange -> onSearchTextChange(action.searchText)
            else -> Unit
        }
    }


    private fun onSearchTextChange(searchText: String) {
        _state.update {
            it.copy(
                searchText = searchText
            )
        }
    }

    private fun onDismissTopicDropDown() {
        _state.update {
            it.copy(
                isTopicSuggestionsVisible = false
            )
        }
    }

    private fun onAddButtonClick() {
        _state.update {
            it.copy(
                isTopicTextVisible = true
            )
        }
    }

    private fun onRemoveTopicClick(topic: String) {
        viewModelScope.launch {
            _state.update { it.copy(
                isTopicTextVisible = false,
                isTopicSuggestionsVisible = false,
                searchText = ""
            ) }

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