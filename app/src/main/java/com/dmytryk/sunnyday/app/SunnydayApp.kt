package com.dmytryk.sunnyday.app

import android.app.Application
import io.reactivex.plugins.RxJavaPlugins

class SunnydayApp: Application(){
    override fun onCreate() {
        super.onCreate()
        instance = this
        RxJavaPlugins.setErrorHandler {  }
    }


    companion object {
        lateinit var instance: SunnydayApp
            private set
    }
}