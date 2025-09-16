package com.cslori.echojournal.echos.presentation.create_echo.fakes

import com.cslori.echojournal.echos.domain.echo.Mood
import com.cslori.echojournal.echos.domain.settings.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestSettingsPreferences : SettingsPreferences {
    private var defaultTopics = listOf<String>()
    private var defaultMood = Mood.NEUTRAL

    override suspend fun saveDefaultTopics(topics: List<String>) {
        defaultTopics = topics
    }

    override fun observeDefaultTopics(): Flow<List<String>> = flowOf(defaultTopics)

    override suspend fun saveDefaultMood(mood: Mood) {
        defaultMood = mood
    }

    override fun observeDefaultMood(): Flow<Mood> = flowOf(defaultMood)

    // Helper methods for testing
    fun setDefaultTopics(topics: List<String>) {
        defaultTopics = topics
    }

    fun setDefaultMood(mood: Mood) {
        defaultMood = mood
    }

    fun reset() {
        defaultTopics = emptyList()
        defaultMood = Mood.NEUTRAL
    }
}