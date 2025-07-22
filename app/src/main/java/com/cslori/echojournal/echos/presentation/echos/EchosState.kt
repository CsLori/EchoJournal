package com.cslori.echojournal.echos.presentation.echos

import com.cslori.echojournal.R
import com.cslori.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.cslori.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import com.cslori.echojournal.core.util.UiText
import com.cslori.echojournal.echos.presentation.echos.models.DaySection
import com.cslori.echojournal.echos.presentation.echos.models.EchoFilterChip
import com.cslori.echojournal.echos.presentation.echos.models.MoodChipContent
import com.cslori.echojournal.echos.presentation.models.EchoUi
import com.cslori.echojournal.echos.presentation.models.MoodUi

data class EchosState(
    val echos: Map<UiText, List<EchoUi>> = emptyMap(),
    val hasEchosRecorded: Boolean = false,
    val hasActiveTopicFilters: Boolean = false,
    val hasActiveMoodFilters: Boolean = false,
    val isLoading: Boolean = false,
    val moods: List<Selectable<MoodUi>> = emptyList(),
    val topics: List<Selectable<String>> = listOf("Love", "Happy", "Work").asUnselectedItems(),
    val moodChipContent: MoodChipContent = MoodChipContent(),
    val selectedEchoFilterChip: EchoFilterChip? = null,
    val topicChipTitle: UiText = UiText.StringResource(R.string.all_topics)
) {
    val echoDaySections = echos.toList().map { (dateHeader, echos) ->
        DaySection(
            dateHeader = dateHeader,
            echos = echos
        )

    }
}