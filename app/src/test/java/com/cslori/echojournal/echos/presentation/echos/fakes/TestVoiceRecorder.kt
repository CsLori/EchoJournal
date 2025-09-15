package com.cslori.echojournal.echos.presentation.echos.fakes

import com.cslori.echojournal.echos.domain.recording.RecordingDetails
import com.cslori.echojournal.echos.domain.recording.VoiceRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestVoiceRecorder : VoiceRecorder {
    private val _recordingDetails = MutableStateFlow(RecordingDetails())
    override val recordingDetails: StateFlow<RecordingDetails> = _recordingDetails

    var isStarted = false
        private set
    var isPaused = false
        private set
    var isStopped = false
        private set
    var isResumed = false
        private set
    var isCancelled = false
        private set

    override fun start() {
        isStarted = true
        isPaused = false
        isStopped = false
        isCancelled = false
    }

    override fun pause() {
        isPaused = true
    }

    override fun stop() {
        isStopped = true
    }

    override fun resume() {
        isResumed = true
        isPaused = false
    }

    override fun cancel() {
        isCancelled = true
        isStarted = false
    }

    fun reset() {
        isStarted = false
        isPaused = false
        isStopped = false
        isResumed = false
        isCancelled = false
    }
}