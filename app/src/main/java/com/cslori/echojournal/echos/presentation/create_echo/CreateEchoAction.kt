package com.cslori.echojournal.echos.presentation.create_echo

import com.cslori.echojournal.echos.presentation.echos.models.TrackSizeInfo
import com.cslori.echojournal.echos.presentation.models.MoodUi

sealed interface CreateEchoAction {
    data object NavigateBackClick : CreateEchoAction
    data class TitleTextChange(val text: String) : CreateEchoAction
    data class TopicTextChange(val text: String) : CreateEchoAction
    data class NoteTextChange(val text: String) : CreateEchoAction
    data object SelectMoodClick : CreateEchoAction
    data object DismissMoodSelector : CreateEchoAction
    data class MoodClick(val mood: MoodUi) : CreateEchoAction
    data object ConfirmMood : CreateEchoAction
    data class TopicClick(val topic: String) : CreateEchoAction
    data object DismissTopiSuggestions : CreateEchoAction
    data object CancelClick : CreateEchoAction
    data object SaveClick : CreateEchoAction
    data object PlayAudioClick : CreateEchoAction
    data object PauseAudioClick : CreateEchoAction
    data class TrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : CreateEchoAction
    data class RemoveTopicClick(val topic: String) : CreateEchoAction
    data object GoBack : CreateEchoAction
    data object DismissConfirmLeaveDialog : CreateEchoAction
}