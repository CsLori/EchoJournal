package com.cslori.echojournal.echos.di

import com.cslori.echojournal.echos.data.recording.AndroidVoiceRecorder
import com.cslori.echojournal.echos.domain.recording.VoiceRecorder
import com.cslori.echojournal.echos.presentation.echos.EchosViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val echoModule = module {
    single {
        AndroidVoiceRecorder(
            context = androidApplication(),
            applicationScope = get()
        )
    } bind VoiceRecorder::class

    viewModelOf(::EchosViewModel)
}