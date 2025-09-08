package com.cslori.echojournal.echos.data.echo

import com.cslori.echojournal.core.database.echo.EchoEntity
import com.cslori.echojournal.core.database.echo_topic_relation.EchoWithTopics
import com.cslori.echojournal.core.database.topic.TopicEntity
import com.cslori.echojournal.echos.domain.echo.Echo
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun EchoWithTopics.toEcho(): Echo {
    return Echo(
        mood = echo.mood,
        title = echo.title,
        note = echo.note,
        topics = topics.map { it.topic },
        audioFilePath = echo.audioFilePath,
        audioPlaybackLength = echo.audioPlaybackLength.milliseconds,
        audioAmplitudes = echo.audioAmplitudes,
        recordedAt = Instant.ofEpochMilli(echo.recordedAt),
        id = echo.echoId
    )
}

fun Echo.toEchoWithTopics(): EchoWithTopics {
    return EchoWithTopics(
        echo = EchoEntity(
            echoId = id ?: 0,
            title = title,
            mood = mood,
            recordedAt = recordedAt.toEpochMilli(),
            note = note,
            audioAmplitudes = audioAmplitudes,
            audioFilePath = audioFilePath,
            audioPlaybackLength = audioPlaybackLength.inWholeMilliseconds
        ),
        topics = topics.map { TopicEntity(topic = it) }
    )
}