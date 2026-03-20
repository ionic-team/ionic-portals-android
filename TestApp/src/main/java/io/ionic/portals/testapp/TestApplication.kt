package io.ionic.portals.testapp

import android.app.Application
import android.content.Context
import android.util.Log

import io.ionic.liveupdateprovider.LiveUpdateError.InvalidConfiguration
import io.ionic.liveupdateprovider.LiveUpdateError.SyncFailed
import io.ionic.liveupdateprovider.LiveUpdateManager
import io.ionic.liveupdateprovider.LiveUpdateProvider
import io.ionic.liveupdateprovider.LiveUpdateProviderRegistry
import io.ionic.liveupdateprovider.SyncCallback
import io.ionic.liveupdateprovider.SyncResult
import io.ionic.portals.PortalManager
import java.io.File


/**
 * Mock implementation of LiveUpdateManager for testing purposes.
 * Allows testing config parsing and sync behavior without actual network requests.
 */
internal class MockLiveUpdateManager(
    private val appId: String?,
    private val channel: String?,
    private val latestAppDir: File?,
    private val shouldFail: Boolean,
    private val failureDetails: String,
    private val didUpdate: Boolean
) : LiveUpdateManager {
    override fun sync(callback: SyncCallback?) {
        // Simulate async behavior with a small delay
        Thread(Runnable {
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (this.shouldFail) {
                val error = SyncFailed(this.failureDetails, null)
                callback?.onError(error)
            } else {
                val result = object : SyncResult {
                    override val didUpdate: Boolean = this@MockLiveUpdateManager.didUpdate
                }
                callback?.onComplete(result)
            }
        }).start()
    }

    override val latestAppDirectory: File?
        get() = this.latestAppDir
}


class MockLiveUpdateProvider(override val id: String) : LiveUpdateProvider {
    @Throws(InvalidConfiguration::class)
    override fun createManager(context: Context, config: Map<String, Any>?): LiveUpdateManager {

        val data: Map<String, Any> = config ?: emptyMap()
        var shouldFail = false
        val shouldFailObj = data["shouldFail"]
        if (shouldFailObj is Boolean) {
            shouldFail = shouldFailObj
        }

        var failureDetails = "Mock sync failed"
        val failureDetailsObj = data["failureDetails"]
        if (failureDetailsObj is String) {
            failureDetails = failureDetailsObj
        }

        var didUpdate = false
        val didUpdateObj = data["didUpdate"]
        if (didUpdateObj is Boolean) {
            didUpdate = didUpdateObj
        }

        // For testing purposes, we can point to a static directory that simulates the latest app version.
        // This was gotten from the logs after a successful sync with the real provider
        val filePath =
            "/data/user/0/io.ionic.portals.ecommercewebapp/files/ionic_apps/3fde24f8/5966bde5-da2e-4b40-8487-2b0fef7c458b"
        val latestAppDir = File(filePath)

        return MockLiveUpdateManager(
            data["appId"] as? String,
            data["channel"] as? String,
            latestAppDir,  // latestAppDir
            shouldFail,
            failureDetails,
            didUpdate
        )
    }
}


class TestApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        PortalManager.register(BuildConfig.PORTALS_KEY)
        Log.d("TestApplication", "Registered portal with key: ${BuildConfig.PORTALS_KEY}")
        val portalBuilder = PortalManager.newPortal("testportal")



        // Register provider
        LiveUpdateProviderRegistry.register(MockLiveUpdateProvider("mock"))

        // Resolve the provider where you want in the app
        val provider = LiveUpdateProviderRegistry.resolve("mock")
        if (provider == null) {
            Log.e("TestApplication", "Failed to register MockLiveUpdateProvider")
        } else {
            Log.d("TestApplication", "Successfully registered MockLiveUpdateProvider with ID: ${provider.id}")
        }

        // create the 3rd party manager
        val manager = provider?.createManager(
            this,
            mapOf(
                "appId" to "testAppId",
                "channel" to "testChannel",
                "shouldFail" to false,
                "failureDetails" to "Simulated sync failure",
                "didUpdate" to true,
                "endpoint" to "https://cloud.provider.io",
                "apiKey" to "<PROVIDER_API_KEY>"
            )
        )
        if (manager == null) {
            Log.e("TestApplication", "Failed to create LiveUpdateManager from MockLiveUpdateProvider")
        } else {
            Log.d("TestApplication", "Successfully created LiveUpdateManager from MockLiveUpdateProvider")


            // set the 3rd party manager
            portalBuilder.setLiveUpdateManager(this.applicationContext, manager);
        }

        val portal = portalBuilder.create()
    }
}