package io.ionic.portals.composetestapp

import android.app.Application
import io.ionic.portals.PortalManager

class TestApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        PortalManager.register(BuildConfig.PORTALS_KEY)
    }
}