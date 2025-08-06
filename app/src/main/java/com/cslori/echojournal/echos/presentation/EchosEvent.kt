package com.cslori.echojournal.echos.presentation

import com.cslori.echojournal.echos.domain.recording.RecordingDetails

sealed interface EchosEvent {
    data object RequestAudioPermission : EchosEvent
    data object RecordingTooShort : EchosEvent
    data class DoneRecording(val details: RecordingDetails) : EchosEvent
}