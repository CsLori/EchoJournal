package com.cslori.echojournal.core.util

import androidx.compose.ui.text.intl.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.formatMMSS(): String {
    val totalSeconds = this.toLong(DurationUnit.SECONDS)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format(
        locale = java.util.Locale.getDefault(),
        format = "%02d:%02d",
        minutes,
        seconds
    )
}