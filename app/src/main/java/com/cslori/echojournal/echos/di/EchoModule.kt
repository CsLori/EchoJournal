package com.cslori.echojournal.echos.di

import com.cslori.echojournal.echos.data.recording.AndroidVoiceRecorder
import com.cslori.echojournal.echos.data.recording.InternalRecordingStorage
import com.cslori.echojournal.echos.domain.recording.RecordingStorage
import com.cslori.echojournal.echos.domain.recording.VoiceRecorder
import com.cslori.echojournal.echos.presentation.create_echo.CreateEchoViewModel
import com.cslori.echojournal.echos.presentation.echos.EchosViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val echoModule = module {
    singleOf(::AndroidVoiceRecorder) bind VoiceRecorder::class
    singleOf(::InternalRecordingStorage) bind RecordingStorage::class

    viewModelOf(::EchosViewModel)
    viewModelOf(::CreateEchoViewModel)
}