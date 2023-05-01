package io.ionic.portals

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.getcapacitor.*
import io.ionic.liveupdates.LiveUpdateManager
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.reflect.KVisibility

/**
 * An Android [Fragment](https://developer.android.com/reference/androidx/fragment/app/Fragment) class
 * containing an instance of a [Portal] to load in a web view supported by Capacitor.
 *
 * Example usage (kotlin):
 * ```kotlin
 * val fragment: PortalFragment = PortalFragment(myPortal)
 * ```
 *
 * Example usage (java):
 * ```java
 * PortalFragment fragment = new PortalFragment(myPortal);
 * ```
 *
 */
open class PortalFragment : Fragment {
    val PORTAL_NAME = "PORTALNAME"
    var portal: Portal? = null
    var liveUpdateFiles: File? = null
    var onBridgeAvailable: ((bridge: Bridge) -> Unit)? = null
    var webVitalsCallback: ((WebVitals.Metric, Long) -> Unit)? = null

    private var bridge: Bridge? = null
    private var keepRunning = true
    private val initialPlugins: MutableList<Class<out Plugin?>> = ArrayList()
    private val initialPluginInstances: MutableList<Plugin> = ArrayList()
    private var config: CapConfig? = null
    private val webViewListeners: MutableList<WebViewListener> = ArrayList()
    private var subscriptions = mutableMapOf<String, Int>()
    private var pubSub = PortalsPubSub.shared
    private var initialContext: Any? = null

    constructor()

    constructor(portal: Portal?) {
        this.portal = portal
    }

    constructor(portal: Portal?, onBridgeAvailable: (bridge: Bridge) -> Unit) : this(portal, onBridgeAvailable, null)

    constructor(portal: Portal?, webVitalsCallback: ((WebVitals.Metric, Long) -> Unit)) : this(portal, null, webVitalsCallback)

    constructor(portal: Portal?, onBridgeAvailable: ((bridge: Bridge) -> Unit)?, webVitalsCallback: ((WebVitals.Metric, Long) -> Unit)?) {
        this.portal = portal
        this.onBridgeAvailable = onBridgeAvailable
        this.webVitalsCallback = webVitalsCallback
    }

    /**
     * Extends the Android Fragment `onCreateView` lifecycle event.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = if(PortalManager.isRegistered()) R.layout.fragment_portal else R.layout.fragment_unregistered
        return inflater.inflate(layout, container, false)
    }

    /**
     * Extends the Android Fragment `onViewCreated` lifecycle event.
     * At this point in the lifecycle the fragment will attempt to load the Portal content.
     * This is when the fragment will load any provided config or plugins.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load(savedInstanceState)
    }

    /**
     * Extends the Android Fragment 'onDestroy' lifecycle event.
     * At this point in the lifecycle the fragment will attempt to clean up the [Bridge] and
     * unsubscribe any attached Portals message subscriptions.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (bridge != null) {
            bridge?.onDestroy()
            bridge?.onDetachedFromWindow()
        }
        for ((topic, ref) in subscriptions) {
            pubSub.unsubscribe(topic, ref)
        }
    }

    /**
     * Extends the Android Fragment 'onResume' lifecycle event.
     */
    override fun onResume() {
        super.onResume()
        bridge?.app?.fireStatusChange(true)
        bridge?.onResume()
        Logger.debug("App resumed")
    }

    /**
     * Extends the Android Fragment 'onPause' lifecycle event.
     */
    override fun onPause() {
        super.onPause()
        bridge?.onPause()
        Logger.debug("App paused")
    }

    /**
     * Extends the Android Fragment 'onSaveInstanceState' event.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PORTAL_NAME, portal?.name)
    }

    /**
     * Extends the Android Fragment 'onConfigurationChanged' event.
     */
    override fun onConfigurationChanged(@NonNull newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        bridge?.onConfigurationChanged(newConfig)
    }

    /**
     * Add a Capacitor [Plugin] to be loaded with the Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * portalFragment.addPlugin(MyPlugin::class.java)
     * ```
     *
     * Example usage (java):
     * ```java
     * portalFragment.addPlugin(MyPlugin.class);
     * ```
     *
     * @param plugin a plugin to load with the Portal
     */
    fun addPlugin(plugin: Class<out Plugin?>?) {
        initialPlugins.add(plugin!!)
    }

    /**
     * Add a Capacitor [Plugin] instance to be loaded with the Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val myPlugin = MyCapacitorPlugin()
     * portalFragment.addPluginInstance(myPlugin)
     * ```
     *
     * Example usage (java):
     * ```java
     * val myPlugin = new MyCapacitorPlugin();
     * portalFragment.addPluginInstance(myPlugin);
     * ```
     *
     * @param plugin a plugin instance to load with the Portal
     */
    fun addPluginInstance(plugin: Plugin) {
        initialPluginInstances.add(plugin)
    }

    /**
     * Set a Capacitor [CapConfig] to be used to configure the instance of Capacitor used in the Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val config = CapConfig.Builder(context).setLoggingEnabled(true).create()
     * portalFragment.setConfig(config)
     * ```
     *
     * Example usage (java):
     * ```java
     * CapConfig config = new CapConfig.Builder(getContext()).setLoggingEnabled(true).create();
     * portalFragment.setConfig(config);
     * ```
     *
     * @param config the Capacitor config to use
     */
    fun setConfig(config: CapConfig?) {
        this.config = config
    }

    /**
     * Get the Capacitor [Bridge] instance used in the Portal.
     *
     * @return the Capacitor Bridge supporting the web view content
     */
    fun getBridge(): Bridge? {
        return bridge
    }

    /**
     * Adds a Capacitor [WebViewListener] to the Portal to trigger a callback on web view events.
     *
     * @param webViewListener a listener to trigger on web view events
     */
    fun addWebViewListener(webViewListener: WebViewListener) {
        webViewListeners.add(webViewListener)
    }

    /**
     * Set an Initial Context that will be loaded in lieu of one set on the Portal object.
     *
     * @param initialContext the Initial Context information
     */
    fun setInitialContext(initialContext: Any) {
        this.initialContext = initialContext
    }

    /**
     * Get the Initial Context that will be loaded in lieu of one set on the Portal object, if set.
     *
     * @return the Initial Context information
     */
    fun getInitialContext(): Any? {
        return this.initialContext
    }

    /**
     * Reloads the Portal.
     * If Live Updates is used and the web content was updated, the new content will be loaded.
     */
    fun reload() {
        if(portal?.liveUpdateConfig != null) {
            val latestLiveUpdateFiles = LiveUpdateManager.getLatestAppDirectory(requireContext(), portal?.liveUpdateConfig?.appId!!)
            if (latestLiveUpdateFiles != null) {
                if (liveUpdateFiles == null || liveUpdateFiles!!.path != latestLiveUpdateFiles.path) {
                    liveUpdateFiles = latestLiveUpdateFiles

                    // Reload the bridge to the new files path
                    bridge?.serverBasePath = liveUpdateFiles!!.path
                    return
                }
            } else {
                liveUpdateFiles = null
                bridge?.setServerAssetPath(portal?.startDir!!)
            }
        }

        // Reload the bridge to the existing start url
        bridge?.reload()
    }

    /**
     * Load the WebView and create the Bridge
     */
    private fun load(savedInstanceState: Bundle?) {
        if (PortalManager.isRegistered()) {
            if (bridge == null) {
                Logger.debug("Loading Bridge with Portal")

                val existingPortalName = savedInstanceState?.getString(PORTAL_NAME, null)
                if (existingPortalName != null && portal == null) {
                    portal = PortalManager.getPortal(existingPortalName)
                }

                if (portal != null) {
                    val startDir: String = portal?.startDir!!
                    initialPlugins.addAll(portal?.plugins!!)
                    initialPluginInstances.addAll(portal?.pluginInstances!!)

                    var configToUse : CapConfig? = null
                    if(config != null) {
                        // If application is provided a programmatic config, opt to use that above all other options
                        configToUse = config
                    }

                    var bridgeBuilder = Bridge.Builder(this)
                        .setInstanceState(savedInstanceState)
                        .setPlugins(initialPlugins)
                        .addPluginInstances(initialPluginInstances)
                        .addWebViewListeners(webViewListeners);

                    if (portal?.liveUpdateConfig != null) {
                        liveUpdateFiles = LiveUpdateManager.getLatestAppDirectory(requireContext(), portal?.liveUpdateConfig?.appId!!)
                        bridgeBuilder = if (liveUpdateFiles != null) {
                            if (config == null) {
                                val configFile = File(liveUpdateFiles!!.path + "/capacitor.config.json")
                                if(configFile.exists()) {
                                    configToUse = CapConfig.loadFromFile(requireContext(), liveUpdateFiles!!.path)
                                }
                            }

                            bridgeBuilder.setServerPath(ServerPath(ServerPath.PathType.BASE_PATH, liveUpdateFiles!!.path))
                        } else {
                            if (config == null) {
                                try {
                                    val configFile = requireContext().assets.open("$startDir/capacitor.config.json")
                                    configToUse = CapConfig.loadFromAssets(requireContext(), startDir)
                                } catch (_: Exception) {}
                            }

                            bridgeBuilder.setServerPath(ServerPath(ServerPath.PathType.ASSET_PATH, startDir))
                        }
                    } else {
                        if (config == null) {
                            try {
                                val configFile = requireContext().assets.open("$startDir/capacitor.config.json")
                                configToUse = CapConfig.loadFromAssets(requireContext(), startDir)
                            } catch (_: Exception) {}
                        }

                        bridgeBuilder = bridgeBuilder.setServerPath(ServerPath(ServerPath.PathType.ASSET_PATH, startDir))
                    }

                    portal?.assetMaps?.let {
                        if (it.isNotEmpty()) {
                            bridgeBuilder = bridgeBuilder.setRouteProcessor(PortalsRouteProcessor(requireContext(),it))
                        }
                    }

                    if(configToUse == null) {
                        configToUse = CapConfig.Builder(requireContext()).setInitialFocus(false).create()
                    }

                    bridgeBuilder = bridgeBuilder.setConfig(configToUse)
                    bridge = bridgeBuilder.create()

                    setupPortalsJS()
                    keepRunning = bridge?.shouldKeepRunning()!!

                    onBridgeAvailable?.let { onBridgeAvailable -> bridge?.let { bridge -> onBridgeAvailable(bridge)} }
                }
            }
        } else if (PortalManager.isRegisteredError()) {
            if(activity != null) {
                val alert = AlertDialog.Builder(activity)
                alert.setMessage("Error validating your key for Ionic Portals. Check your key and try again.")
                alert.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alert.show()
            }
        }
    }

    /**
     * Sets up the supporting JavaScript code that Portals needs on the web view content.
     */
    private fun setupPortalsJS() {
        val initialContext = this.initialContext ?: portal?.initialContext

        val portalInitialContext = JSONObject()
        portalInitialContext.put("name", portal?.name)

        if(initialContext != null) {
            val initialContextValues: JSONObject = when (initialContext) {
                is String -> {
                    try {
                        JSONObject(initialContext)
                    } catch (ex: JSONException) {
                        throw Error("initialContext must be a JSON string or a Map")
                    }
                }
                is Map<*, *> -> {
                    JSONObject(initialContext.toMap())
                }
                else -> {
                    throw Error("initialContext must be a JSON string or a Map")
                }
            }

            portalInitialContext.put("value", initialContextValues)
        }

        portal?.assetMaps?.let { assetmaps ->
            if (assetmaps.isNotEmpty()) {
                val assetMapsJSON = JSONObject()
                for ((_, assetmap) in assetmaps) {
                    assetMapsJSON.put(assetmap.name, assetmap.virtualPath)
                }
                portalInitialContext.put("assets", assetMapsJSON)
            }
        }

        // Add interface for WebVitals interaction
        webVitalsCallback?.let { webvitalsCallback ->
            bridge?.webView?.addJavascriptInterface(WebVitals(portal!!.name, webvitalsCallback), "WebVitals")
        }

        val newWebViewClient = object: BridgeWebViewClient(bridge) {
            var hasMainRun = false
            var hasBeenSetup = false

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                view?.post {
                    run {
                        if (!hasBeenSetup && hasMainRun) {
                            // Add WebVitals javascript to the webview
                            webVitalsCallback?.let { webvitalsCallback ->
                                view.evaluateJavascript(WebVitals(portal!!.name, webvitalsCallback).js, null)
                            }


                            hasBeenSetup = true
                        }

                        hasMainRun = true

                        // Add initial context to the webview
                        view.evaluateJavascript(
                            "window.portalInitialContext = $portalInitialContext", null
                        )
                    }
                }

                return super.shouldInterceptRequest(view, request)
            }
        }

        bridge?.webView?.webViewClient = newWebViewClient
    }

    /**
     * Link a class with methods decorated with the [PortalMethod] annotation to use as Portals
     * message receivers.
     *
     * The name of the method should match the message name used to send messages via the Portal.
     * Alternatively the [PortalMethod] annotation topic property can be used to designate a
     * different name. The registered methods should accept a single String representing the payload
     * of a message sent through the Portal.
     *
     * An instance of [PortalsPubSub] can be provided to override the default behavior of publishing
     * events through [PortalsPubSub.shared].
     *
     * @param messageReceiverParent a class that contains [PortalMethod] annotated functions
     * @param pubSub an instance of [PortalsPubSub]. Defaults to [PortalsPubSub.shared].
     */
    @JvmOverloads
    fun linkMessageReceivers(messageReceiverParent: Any, pubSub: PortalsPubSub = PortalsPubSub.shared) {
        this.pubSub = pubSub
        val members = messageReceiverParent.javaClass.kotlin.members.filter { it.annotations.any { annotation -> annotation is PortalMethod } }

        for (member in members) {
            var methodName = member.name
            for (annotation in member.annotations) {
                if (annotation is PortalMethod && annotation.topic.isNotEmpty()) {
                    methodName = annotation.topic
                }
            }

            if(member.visibility != KVisibility.PUBLIC) {
                throw IllegalAccessException("Portal Method '${member.name}' must be public!")
            }

            when (member.parameters.size) {
                1 -> {
                    val ref = pubSub.subscribe(methodName) { result ->
                        member.call(messageReceiverParent)
                    }
                    subscriptions[methodName] = ref
                }
                2 -> {
                    val ref = pubSub.subscribe(methodName) { result ->
                        member.call(messageReceiverParent, result.data)
                    }
                    subscriptions[methodName] = ref
                }

                else -> {
                    throw IllegalArgumentException("Portal Method '${member.name}' must" +
                            " contain zero parameters or a single String parameter!")
                }
            }
        }
    }
}
