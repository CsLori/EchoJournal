package com.cslori.echojournal.echos.presentation.echos

import com.cslori.echojournal.echos.presentation.echos.models.EchoFilterChip
import com.cslori.echojournal.echos.presentation.echos.models.TrackSizeInfo
import com.cslori.echojournal.echos.presentation.models.MoodUi

sealed interface EchosAction {
    data object MoodChipClick : EchosAction
    data object DismissMoodDropdown : EchosAction
    data class FilterByMood(val moodUi: MoodUi) : EchosAction
    data object TopicChipClick : EchosAction
    data object DismissTopicDropdown : EchosAction
    data class FilterByTopic(val topic: String) : EchosAction
    data object RecordFabClick : EchosAction
    data object RequestPermissionQuickRecording : EchosAction
    data object RecordButtonLongClick : EchosAction
    data object SettingsClick : EchosAction
    data class RemoveFilters(val filterType: EchoFilterChip) : EchosAction
    data class PlayEchoClick(val echoId: Int) : EchosAction
    data object PauseRecordingClick : EchosAction
    data object PauseAudioClick : EchosAction
    data class TrackSizeAvailable(val trackSize: TrackSizeInfo) : EchosAction
    data object AudioPermissionGranted : EchosAction
    data object CancelRecordingClick : EchosAction
    data object ResumeRecordingClick : EchosAction
    data object CompleteRecordingClick : EchosAction

}