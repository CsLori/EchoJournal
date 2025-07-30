package com.cslori.echojournal.app.di

import com.cslori.echojournal.app.EchoJournalApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    // Define your app-level dependencies here
    // For example, you can provide a singleton for the application context
    single<CoroutineScope> {
        (androidApplication() as EchoJournalApp).applicationScope
    }
}