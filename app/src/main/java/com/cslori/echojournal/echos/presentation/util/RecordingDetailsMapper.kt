package com.cslori.echojournal.echos.presentation.util

import com.cslori.echojournal.app.navigation.NavigationRoute
import com.cslori.echojournal.echos.domain.recording.RecordingDetails
import kotlin.time.Duration.Companion.milliseconds

fun RecordingDetails.toCreateEchoRoute(): NavigationRoute.CreateEcho {
    return NavigationRoute.CreateEcho(
        recordingPath = this.filePath
            ?: throw IllegalArgumentException("Recording path cannot be null"),
        duration = this.duration.inWholeMilliseconds,
        amplitudes = this.amplitudes.joinToString(separator = ";")
    )
}

fun NavigationRoute.CreateEcho.toRecordingDetails(): RecordingDetails {
    return RecordingDetails(
        duration = this.duration.milliseconds,
        amplitudes = this.amplitudes.split(";").map { it.toFloat() },
        filePath = recordingPath
    )
}