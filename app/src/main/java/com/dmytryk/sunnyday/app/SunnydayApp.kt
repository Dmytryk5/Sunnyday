package com.dmytryk.sunnyday.app

import android.app.Application

class SunnydayApp: Application(){
    override fun onCreate() {
        super.onCreate()
        instance = this
    }


    companion object {
        lateinit var instance: SunnydayApp
            private set
    }
}