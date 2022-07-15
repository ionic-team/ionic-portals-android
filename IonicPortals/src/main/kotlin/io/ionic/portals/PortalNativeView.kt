package io.ionic.portals

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.webkit.WebView
import androidx.activity.result.ActivityResultCaller
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.getcapacitor.*

class PortalNativeView : WebView, DefaultLifecycleObserver {
    var bridge: Bridge? = null
    var portal: Portal? = null
    var portalId: String? = null
    var viewId: String? = null
    var tag: String? = null
    var wasStopped: Boolean = false

    private var keepRunning = true
    private val initialPlugins: MutableList<Class<out Plugin?>> = ArrayList()
    private var config: CapConfig? = null
    private val webViewListeners: MutableList<WebViewListener> = ArrayList()
    private var subscriptions = mutableMapOf<String, Int>()

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        // get portal info from view xml
        if (attrs != null) {
            readAttributes(context, attrs)
            loadPortal()
        }
    }

    constructor(context: Context, portal: Portal?) : super(context) {
        this.portal = portal
        loadPortal()
    }

    /**
     * Read attributes defined on the Portal View in the layout.
     *
     * @param context
     * @param attrs
     */
    private fun readAttributes(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PortalNativeView, 0, 0)
        portalId = a.getString(R.styleable.PortalNativeView_portalId)
        viewId = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "id")
        tag = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "tag")
        a.recycle()

        if (portalId == null) {
            throw IllegalStateException("Portal views must have a defined portalId")
        } else {
            portal = PortalManager.getPortal(portalId!!)
        }
    }

    override fun reload() {
        bridge?.reload()
    }

    private fun loadPortal() {
        if(!PortalManager.isRegistered()) {
            loadUrl("file:///android_asset/io.ionic.portals.unregistered/index.html")
        } else {
            if (PortalManager.size() == 0) {
                throw Exception("Ionic Portals has not been setup with any Portals!")
            }

            if (portal == null) {
                if (portalId == null) {
                    throw IllegalStateException("Portal views must have a defined portalId")
                }

                portal = PortalManager.getPortal(portalId!!)
            }

            if (bridge == null) {
                Logger.debug("Loading Bridge with Portal")

                val startDir: String = portal!!.startDir
                initialPlugins.addAll(portal!!.plugins)

                bridge = Bridge.Builder(this)
                    .setPlugins(initialPlugins)
                    .setConfig(config)
                    .addWebViewListeners(webViewListeners)
                    .create()

                bridge?.setServerAssetPath(startDir)
                keepRunning = bridge?.shouldKeepRunning()!!
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onPause(owner)
        bridge?.onPause()
    }

    override fun onResume(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onResume(owner)
        bridge?.onResume()
    }

    override fun onStart(owner: LifecycleOwner) {
        if(wasStopped) {
            wasStopped = false
            bridge?.onRestart()
        }

        super.onStart(owner)
        bridge?.onStart()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        bridge?.onStop()
        wasStopped = true
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        bridge?.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        bridge?.onConfigurationChanged(newConfig)
    }
}