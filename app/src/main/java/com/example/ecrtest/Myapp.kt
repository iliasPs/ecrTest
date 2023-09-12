package com.example.ecrtest

import android.app.Application
import com.example.ecrtool.dataHandle.ProcessFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Myapp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Myapp)
            modules(module{
                single {ProcessFlow.getInstance()}
            })
        }
    }
}