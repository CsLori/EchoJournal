package com.cslori.echojournal.echos.presentation.echos.fakes

import com.cslori.echojournal.echos.domain.audio.AudioPlayer
import com.cslori.echojournal.echos.domain.audio.AudioTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestAudioPlayer : AudioPlayer {
    private val _activeTrack = MutableStateFlow<AudioTrack?>(null)
    override val activeTrack: StateFlow<AudioTrack?> = _activeTrack

    var isPaused = false
        private set
    var isResumed = false
        private set
    var isStopped = false
        private set

    override fun play(filePath: String, onComplete: () -> Unit) {
        _activeTrack.value = AudioTrack(isPlaying = true)
        isPaused = false
        isStopped = false
    }

    override fun pause() {
        isPaused = true
        _activeTrack.value = _activeTrack.value?.copy(isPlaying = false)
    }

    override fun resume() {
        isResumed = true
        isPaused = false
        _activeTrack.value = _activeTrack.value?.copy(isPlaying = true)
    }

    override fun stop() {
        isStopped = true
        isPaused = false
        _activeTrack.value = null
    }

    fun reset() {
        isPaused = false
        isResumed = false
        isStopped = false
        _activeTrack.value = null
    }
}