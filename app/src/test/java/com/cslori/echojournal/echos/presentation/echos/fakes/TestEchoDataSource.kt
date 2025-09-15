package com.cslori.echojournal.echos.presentation.echos.fakes

import com.cslori.echojournal.echos.domain.echo.Echo
import com.cslori.echojournal.echos.domain.echo.EchoDataSource
import com.cslori.echojournal.echos.domain.echo.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class TestEchoDataSource : EchoDataSource {
    private val sampleEchos = listOf(
        Echo(
            id = 1,
            mood = Mood.EXCITED,
            title = "Test Echo 1",
            note = "Test note 1",
            topics = listOf("work", "meeting"),
            audioFilePath = "/test/path1.mp3",
            audioPlaybackLength = 30.seconds,
            audioAmplitudes = listOf(0.1f, 0.2f, 0.3f),
            recordedAt = Instant.now()
        )
    )

    private val insertedEchos = mutableListOf<Echo>()

    override fun observeEchos(): Flow<List<Echo>> = flowOf(sampleEchos)

    override fun observeTopics(): Flow<List<String>> =
        flowOf(listOf("work", "meeting", "personal"))

    override fun searchTopics(query: String): Flow<List<String>> =
        flowOf(listOf("work", "meeting"))

    override suspend fun insertEcho(echo: Echo) {
        insertedEchos.add(echo)
    }

    // Helper methods for testing
    fun getInsertedEchos(): List<Echo> = insertedEchos.toList()

    fun clearInsertedEchos() {
        insertedEchos.clear()
    }
}