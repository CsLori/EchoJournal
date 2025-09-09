package com.cslori.echojournal.echos.presentation.util

import com.cslori.echojournal.echos.domain.echo.Echo
import com.cslori.echojournal.echos.presentation.echos.models.PlaybackState
import com.cslori.echojournal.echos.presentation.models.EchoUi
import com.cslori.echojournal.echos.presentation.models.MoodUi
import kotlin.time.Duration

fun Echo.toEchoUi(
    currentPlaybackDuration : Duration = Duration.ZERO,
    playbackState: PlaybackState = PlaybackState.STOPPED
) : EchoUi {
    return EchoUi(
        id = id!!,
        title = title,
        moodUi = MoodUi.valueOf(mood.name),
        recordedAt = recordedAt,
        note = note,
        topics = topics,
        playbackTotalDuration = audioPlaybackLength,
        amplitudes = audioAmplitudes,
        audioFilePath = audioFilePath,
        playbackState = playbackState,
        playbackCurrentDuration = currentPlaybackDuration
    )
}