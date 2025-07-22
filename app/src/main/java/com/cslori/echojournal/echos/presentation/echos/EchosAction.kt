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
    data object FabClick : EchosAction
    data object FabLongClick : EchosAction
    data object SettingsClick : EchosAction
    data class RemoveFilters(val filterType: EchoFilterChip) : EchosAction
    data class PlayEchoClick(val echoId: Int) : EchosAction
    data object PauseClick : EchosAction
    data class TrackSizeAvailable(val trackSize: TrackSizeInfo) : EchosAction

}