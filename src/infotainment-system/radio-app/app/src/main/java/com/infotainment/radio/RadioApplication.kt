package com.infotainment.radio

import android.app.Application

/**
 * Application class for the Infotainment Radio system.
 * Initializes core services and manages application lifecycle.
 */
class RadioApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RadioApplication
            private set
    }
}
