package com.cslori.echojournal.echos.presentation.create_echo

import com.cslori.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.cslori.echojournal.echos.presentation.echos.models.PlayBackState
import com.cslori.echojournal.echos.presentation.models.MoodUi
import kotlin.time.Duration

data class CreateEchoState(
    val titleText: String = "",
    val addTopicText: String = "",
    val topics: List<String> = listOf("Love", "Happiness", "Sadness", "Anger", "Fear"),
    val noteText: String = "",
    val showMoodSelector: Boolean = true,
    val selectedMood: MoodUi = MoodUi.NEUTRAL,
    val showTopicSuggestions: Boolean = false,
    val mood: MoodUi? = null,
    val searchResults: List<Selectable<String>> = emptyList(),
    val showCreateTopicOption: Boolean = true,
    val canSaveEcho: Boolean = false,
    val playbackAmplitudes: List<Float> = emptyList(),
    val playbackTotalDuration: Duration = Duration.ZERO,
    val playBackState: PlayBackState = PlayBackState.STOPPED,
    val durationPlayed: Duration = Duration.ZERO,
    val showConfirmLeaveDialog: Boolean = false,
) {
    val durationPlayedRatio =
        (durationPlayed / playbackTotalDuration).toFloat()
}