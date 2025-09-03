package com.cslori.echojournal.echos.presentation.create_echo

sealed interface CreateEchoEvent {
    data object FailedToSaveFile : CreateEchoEvent
}