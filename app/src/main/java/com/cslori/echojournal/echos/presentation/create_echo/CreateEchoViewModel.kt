package com.cslori.echojournal.echos.presentation.create_echo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateEchoViewModel : ViewModel() {

    private var _state = MutableStateFlow(CreateEchoState())
    val state = _state.asStateFlow()

    fun onAction(action: CreateEchoAction) {
        when (action) {
            CreateEchoAction.CancelClick -> TODO()
            CreateEchoAction.ConfirmMood -> TODO()
            CreateEchoAction.CreateNewTopicClick -> TODO()
            CreateEchoAction.DismissMoodSelector -> TODO()
            CreateEchoAction.DismissTopiClick -> TODO()
            is CreateEchoAction.MoodClick -> TODO()
            CreateEchoAction.NavigateBackClick -> TODO()
            is CreateEchoAction.NotesTextChange -> TODO()
            CreateEchoAction.PauseAudioClick -> TODO()
            CreateEchoAction.PlayAudioClick -> TODO()
            is CreateEchoAction.RemoveTopicClick -> TODO()
            CreateEchoAction.SaveClick -> TODO()
            CreateEchoAction.SelectMoodClick -> TODO()
            is CreateEchoAction.TitleTextChange -> TODO()
            is CreateEchoAction.TopicClick -> TODO()
            is CreateEchoAction.TopicTextChange -> TODO()
            is CreateEchoAction.TrackSizeAvailable -> TODO()
        }
    }
}