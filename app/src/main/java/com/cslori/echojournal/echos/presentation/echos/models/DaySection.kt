package com.cslori.echojournal.echos.presentation.echos.models

import com.cslori.echojournal.core.util.UiText
import com.cslori.echojournal.echos.presentation.models.EchoUi

data class DaySection(
    val dateHeader: UiText,
    val echos: List<EchoUi>
)