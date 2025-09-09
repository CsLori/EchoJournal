package com.cslori.echojournal.echos.presentation.preview

import com.cslori.echojournal.echos.presentation.echos.models.PlaybackState
import com.cslori.echojournal.echos.presentation.models.EchoUi
import com.cslori.echojournal.echos.presentation.models.MoodUi
import java.time.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

data object PreviewModels {

    val echoUi = EchoUi(
        id = 0,
        title = "My audio memo",
        moodUi = MoodUi.STRESSED,
        recordedAt = Instant.now(),
        note = (1..50).joinToString(" ") { "Hello" },
        topics = listOf("Love", "Work"),
        amplitudes = (1..30).map { Random.nextFloat() },
        playbackTotalDuration = 250.seconds,
        playbackCurrentDuration = 120.seconds,
        playbackState = PlaybackState.PAUSED,
        audioFilePath = ""
    )
}