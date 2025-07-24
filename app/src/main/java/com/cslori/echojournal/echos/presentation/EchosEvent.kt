package com.cslori.echojournal.echos.presentation

sealed interface EchosEvent {
    data object RequestAudioPermission : EchosEvent
}