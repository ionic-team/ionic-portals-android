package io.ionic.portals.testapp

import android.app.Application
import android.content.Context
import android.util.Log

import io.ionic.liveupdatesprovider.LiveUpdatesError.InvalidConfiguration
import io.ionic.liveupdatesprovider.LiveUpdatesError.SyncFailed
import io.ionic.liveupdatesprovider.LiveUpdatesManager
import io.ionic.liveupdatesprovider.LiveUpdatesProvider
import io.ionic.liveupdatesprovider.LiveUpdatesRegistry
import io.ionic.liveupdatesprovider.SyncCallback
import io.ionic.liveupdatesprovider.models.ProviderConfig
import io.ionic.liveupdatesprovider.models.SyncResult
import io.ionic.portals.PortalManager
import java.io.File


/**
 * Mock implementation of LiveUpdatesManager for testing purposes.
 * Allows testing config parsing and sync behavior without actual network requests.
 */
internal class MockLiveUpdatesManager(
    private val appId: String?,
    private val channel: String?,
    private val latestAppDir: File?,
    private val shouldFail: Boolean,
    private val failureDetails: String,
    private val didUpdate: Boolean
) : LiveUpdatesManager {
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
                if (callback != null) {
                    callback.onError(error)
                }
            } else {
                val result = SyncResult(this.didUpdate, this.latestAppDir)
                if (callback != null) {
                    callback.onComplete(result)
                }
            }
        }).start()
    }

    override fun latestAppDirectory(): File? {
        return this.latestAppDir
    }
}


class MockLiveUpdatesProvider(override val id: String) : LiveUpdatesProvider {
    @Throws(InvalidConfiguration::class)
    override fun createManager(context: Context, config: ProviderConfig): LiveUpdatesManager {

        val data: MutableMap<String?, Any?> = config.data as MutableMap<String?, Any?>
        var shouldFail = false
        val shouldFailObj = data.get("shouldFail")
        if (shouldFailObj is Boolean) {
            shouldFail = shouldFailObj
        }

        var failureDetails = "Mock sync failed"
        val failureDetailsObj = data.get("failureDetails")
        if (failureDetailsObj is String) {
            failureDetails = failureDetailsObj
        }

        var didUpdate = false
        val didUpdateObj = data.get("didUpdate")
        if (didUpdateObj is Boolean) {
            didUpdate = didUpdateObj
        }

        // For testing purposes, we can point to a static directory that simulates the latest app version.
        // This was gotten from the logs after a successful sync with the real provider
        val filePath =
            "/data/user/0/io.ionic.portals.ecommercewebapp/files/ionic_apps/3fde24f8/5966bde5-da2e-4b40-8487-2b0fef7c458b"
        val latestAppDir = File(filePath)

        return MockLiveUpdatesManager(
            data.get("appId") as? String,
            data.get("channel") as? String,
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
        LiveUpdatesRegistry.register(MockLiveUpdatesProvider("mock"))

        // Resolve the provider where you want in the app
        val provider = LiveUpdatesRegistry.resolve("mock")
        if (provider == null) {
            Log.e("TestApplication", "Failed to register MockLiveUpdatesProvider")
        } else {
            Log.d("TestApplication", "Successfully registered MockLiveUpdatesProvider with ID: ${provider.id}")
        }

        // create the 3rd party manager
        val manager = provider?.createManager(
            this,
            ProviderConfig(
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
        )
        if (manager == null) {
            Log.e("TestApplication", "Failed to create LiveUpdatesManager from MockLiveUpdatesProvider")
        } else {
            Log.d("TestApplication", "Successfully created LiveUpdatesManager from MockLiveUpdatesProvider")


            // set the 3rd party manager
            portalBuilder.setLiveUpdateManager(this.applicationContext, manager);
        }

        val portal = portalBuilder.create()
    }
}