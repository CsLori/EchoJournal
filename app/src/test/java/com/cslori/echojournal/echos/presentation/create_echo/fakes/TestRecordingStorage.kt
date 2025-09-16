package com.cslori.echojournal.echos.presentation.create_echo.fakes

import com.cslori.echojournal.echos.domain.recording.RecordingStorage

class TestRecordingStorage : RecordingStorage {
    var shouldFailSave = false
    var savedPaths = mutableListOf<String>()
    var cleanedUpCount = 0

    override suspend fun savePersistently(tempFilePath: String): String? {
        return if (shouldFailSave) {
            null
        } else {
            val savedPath = "/persistent/$tempFilePath"
            savedPaths.add(savedPath)
            savedPath
        }
    }

    override suspend fun cleanUpTemporaryFiles() {
        cleanedUpCount++
    }

    fun reset() {
        shouldFailSave = false
        savedPaths.clear()
        cleanedUpCount = 0
    }
}