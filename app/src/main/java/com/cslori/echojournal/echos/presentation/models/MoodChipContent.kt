package com.cslori.echojournal.echos.presentation.models

import com.cslori.echojournal.R
import com.cslori.echojournal.core.util.UiText

data class MoodChipContent(
    val iconsRes: List<Int> = emptyList(),
    val title: UiText = UiText.StringResource(R.string.all_moods)
)