package com.nes.rgeo

import android.app.Application
import com.nes.rgeo.api.GoogleAPI

class RgeoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        GoogleAPI.with(this)
    }
}