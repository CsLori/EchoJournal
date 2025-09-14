package com.cslori.echojournal.echos.di

import com.cslori.echojournal.echos.data.audio.AndroidAudioPlayer
import com.cslori.echojournal.echos.data.echo.RoomEchoDataSource
import com.cslori.echojournal.echos.data.recording.AndroidVoiceRecorder
import com.cslori.echojournal.echos.data.recording.InternalRecordingStorage
import com.cslori.echojournal.echos.data.settings.DataSoreSettings
import com.cslori.echojournal.echos.domain.audio.AudioPlayer
import com.cslori.echojournal.echos.domain.echo.EchoDataSource
import com.cslori.echojournal.echos.domain.recording.RecordingStorage
import com.cslori.echojournal.echos.domain.recording.VoiceRecorder
import com.cslori.echojournal.echos.domain.settings.SettingsPreferences
import com.cslori.echojournal.echos.presentation.create_echo.CreateEchoViewModel
import com.cslori.echojournal.echos.presentation.echos.EchosViewModel
import com.cslori.echojournal.echos.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val echoModule = module {
    singleOf(::AndroidVoiceRecorder) bind VoiceRecorder::class
    singleOf(::InternalRecordingStorage) bind RecordingStorage::class
    singleOf(::AndroidAudioPlayer) bind AudioPlayer::class
    singleOf(::RoomEchoDataSource) bind EchoDataSource::class
    singleOf(::DataSoreSettings)  bind SettingsPreferences::class

    viewModelOf(::EchosViewModel)
    viewModelOf(::CreateEchoViewModel)
    viewModelOf(::SettingsViewModel)
}