package io.ionic.portals

import android.content.Context
import com.getcapacitor.Plugin
import io.ionic.liveupdates.LiveUpdate
import io.ionic.liveupdates.LiveUpdateManager

/**
 * A class representing a Portal that contains information about the web content to load and any
 * associated plugins used by the Portal. It is discouraged to use this class directly to create
 * a Portal and instead use [PortalBuilder] or [PortalManager] to construct a new instance.
 *
 * Example usage (kotlin):
 * ```kotlin
 * val name: String = "Hello World"
 * val portal: Portal = Portal(name)
 * ```
 *
 * Example usage (java):
 * ```java
 * String name = "Hello World";
 * Portal portal = new Portal(name);
 * ```
 *
 * @property name the name of the Portal
 */
class Portal(val name: String) {
    /**
     * Capacitor [Plugin] registered with the Portal.
     */
    internal val plugins = ArrayList<Class<out Plugin?>>()

    /**
     * Capacitor [Plugin] instances added to the Portal.
     */
    internal val pluginInstances = ArrayList<Plugin>()

    /**
     * Asset Maps registered with the Portal.
     */
    internal var assetMaps = LinkedHashMap<String, AssetMap>()

    /**
     * Initialize the Portal and add the PortalsPlugin by default.
     */
    init {
        this.plugins.add(PortalsPlugin::class.java)
    }

    /**
     * The initial context to pass to the web view.
     *
     * @return a JSON string or a Map
     */
    var initialContext: Any? = null
        internal set

    /**
     * The [PortalFragment] type used by a [PortalView] when using Portals directly in Android layouts/XML.
     */
    var portalFragmentType: Class<out PortalFragment?> = PortalFragment::class.java

    /**
     * The start directory of the portal web app. Portals will use the name of the Portal by default
     * if this value is not set.
     */
    var startDir: String = ""
        get() = if (field.isEmpty()) name else field

    /**
     * A LiveUpdate config, if live updates is being used.
     */
    var liveUpdateConfig: LiveUpdate? = null
        set(value) {
            field = value
            if (value != null) {
                if(value.assetPath == null) {
                    value.assetPath = this.startDir
                }
            }
        }

    /**
     * Whether to run a live update sync when the portal is added to the manager.
     */
    var liveUpdateOnAppLoad: Boolean = true

    /**
     * Add a Capacitor [Plugin] to be loaded with this Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * portal.addPlugin(MyPlugin::class.java)
     * ```
     *
     * Example usage (java):
     * ```java
     * portal.addPlugin(MyPlugin.class);
     * ```
     *
     * @param plugin a Plugin to be used with the Portal
     */
    fun addPlugin(plugin: Class<out Plugin?>) {
        if(plugin != PortalsPlugin::class.java) {
            plugins.add(plugin)
        }
    }

    /**
     * Add multiple Capacitor [Plugin] to be loaded with this Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val list: List<Class<out Plugin?>> = listOf(
     *     FooPlugin::class.java,
     *     BarPlugin::class.java,
     *     BazPlugin::class.java
     * )
     *
     * portal.addPlugins(list)
     * ```
     *
     * Example usage (java):
     * ```java
     * List<Class<? extends Plugin>> list = Arrays.asList(
     *     FooPlugin.class,
     *     BarPlugin.class,
     *     BazPlugin.class
     * );
     *
     * portal.addPlugins(list);
     * ```
     *
     * @param plugins a list of Plugins to be used with the Portal
     */
    fun addPlugins(plugins: List<Class<out Plugin?>>) {
        plugins.forEach {
            this.addPlugin(it)
        }
    }

    /**
     * Add a Capacitor [Plugin] instance to be loaded with this Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val myPlugin = MyCapacitorPlugin()
     * portal.addPluginInstance(myPlugin)
     * ```
     *
     * Example usage (java):
     * ```java
     * val myPlugin = new MyCapacitorPlugin();
     * portal.addPluginInstance(myPlugin);
     * ```
     *
     * @param plugin a Plugin instance to be used with the Portal
     */
    fun addPluginInstance(plugin: Plugin) {
        pluginInstances.add(plugin)
    }

    /**
     * Add multiple Capacitor [Plugin] instances to be loaded with this Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val list: List<Plugin> = listOf(
     *     MyCapacitorPlugin(),
     *     MySecondCapacitorPlugin(),
     *     MyThirdCapacitorPlugin()
     * )
     *
     * portal.addPluginInstances(list)
     * ```
     *
     * Example usage (java):
     * ```java
     * List<Plugin> list = Arrays.asList(
     *     new MyCapacitorPlugin(),
     *     new MySecondCapacitorPlugin(),
     *     new MyThirdCapacitorPlugin()
     * );
     *
     * portal.addPluginInstances(list);
     * ```
     *
     *
     * @param plugins a list of Plugin instances to be used with the Portal
     */
    fun addPluginInstances(plugins: List<Plugin>) {
        pluginInstances.addAll(plugins)
    }

    /**
     * Add multiple [AssetMap] instances to be loaded with this Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val assetMaps = LinkedHashMap<String, AssetMap>();
     * assetMaps["images"] = AssetMap("images", "/shared/images", "images")
     * portal.addAssetMaps(assetMaps)
     * ```
     *
     * Example usage (java):
     * ```java
     * LinkedHashMap<String, AssetMap> assetMaps = new LinkedHashMap<String, AssetMap>();
     * assetMaps.put("images", new AssetMap("images","/shared/images","images"));
     * portal.addAssetMaps(assetMaps);
     * ```
     *
     * @param assetMaps a list of Plugin instances to be used with the Portal
     */
    fun addAssetMaps(assetMaps: LinkedHashMap<String, AssetMap>) {
        this.assetMaps.putAll(assetMaps)
    }

    /**
     * Sets the initial context to pass to the web view.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val map: Map<String, Any> = mapOf(
     *     "foo" to "bar",
     *     "ionic" to "portals"
     *     "num" to 42
     * )
     *
     * portal.setInitialContext(map)
     * ```
     *
     * Example usage (java):
     * ```java
     * Map<String, Object> map = Map.ofEntries(
     *     new AbstractMap.SimpleEntry<String, @NotNull Object>("foo", "bar"),
     *     new AbstractMap.SimpleEntry<String, @NotNull Object>("ionic", "portals"),
     *     new AbstractMap.SimpleEntry<String, @NotNull Object>("num", 42)
     * );
     *
     * portal.setInitialContext(map);
     * ```
     *
     * @param initialContext A map containing key/pair values that will be converted to a JavaScript object in the web view
     */
    fun setInitialContext(initialContext: Map<String, Any>) {
        this.initialContext = initialContext
    }

    /**
     * Sets the initial context to pass to the web view.
     *
     * Example usage (kotlin):
     * ```kotlin
     * portal.setInitialContext("{\"foo\": \"bar\"}")
     * ```
     *
     * Example usage (java):
     * ```java
     * portal.setInitialContext("{\"foo\": \"bar\"}");
     * ```
     *
     * @param initialContext a JSON string that will be converted to a JavaScript object in the web view
     */
    fun setInitialContext(initialContext: String) {
        this.initialContext = initialContext
    }

}

/**
 * A class used to create [Portal] instances.
 * It follows a [Builder Pattern](https://en.wikipedia.org/wiki/Builder_pattern) and can be used in
 * situations where you want to programmatically create a Portal at runtime instead of using one directly
 * in an XML layout.
 *
 * Example usage (kotlin):
 * ```kotlin
 * val portal: Portal = PortalBuilder("myPortal")
 *     .addPlugin(MyCapacitorPlugin::class.java)
 *     .setPortalFragmentType(MyFadeInOutPortalFragment::class.java)
 *     .setInitialContext(mapOf("myVariableFromAndroid" to 42))
 *     .setStartDir("web_app")
 *     .create()
 * ```
 *
 * Example usage (java):
 * ```java
 * Portal portal = new PortalBuilder("myPortal")
 *     .addPlugin(MyCapacitorPlugin.class)
 *     .setPortalFragmentType(MyFadeInOutPortalFragment.class)
 *     .setInitialContext(Map.of("myVariableFromAndroid", 42))
 *     .setStartDir("web_app")
 *     .create();
 * ```
 *
 * @property name the name of the Portal
 */
class PortalBuilder(val name: String) {
    private var _startDir: String? = null
    private var plugins = mutableListOf<Class<out Plugin?>>()
    private var pluginInstances = mutableListOf<Plugin>()
    private var assetMaps = LinkedHashMap<String, AssetMap>()
    private var initialContext: Any? = null
    private var portalFragmentType: Class<out PortalFragment?> = PortalFragment::class.java
    private var onCreate: (portal: Portal) -> Unit = {}
    private var liveUpdateConfig: LiveUpdate? = null

    internal constructor(name: String, onCreate: (portal: Portal) -> Unit) : this(name) {
        this.onCreate = onCreate;
    }

    /**
     * Set the directory of the Portal.
     * This directory is the on device directory of where your web application is located.
     *
     * Example usage (kotlin):
     * ```kotlin
     * builder = builder.setStartDir("/path/to/web/application/")
     * ```
     *
     * Example usage (java):
     * ```java
     * builder = builder.setStartDir("/path/to/web/application/");
     * ```
     *
     * @param startDir the start directory the Portal should load
     * @return the instance of the PortalBuilder with the start directory set
     */
    fun setStartDir(startDir: String): PortalBuilder {
        this._startDir = startDir
        return this
    }

    /**
     * Add a plugin to be loaded with the Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * builder = builder.addPlugin(MyPlugin::class.java)
     * ```
     *
     * Example usage (java):
     * ```java
     * builder = builder.addPlugin(MyPlugin.class);
     * ```
     *
     * @param plugin the plugin class to add to the portal
     * @return the instance of the PortalBuilder with the plugin added
     */
    fun addPlugin(plugin: Class<out Plugin?>): PortalBuilder {
        plugins.add(plugin)
        return this
    }

    /**
     * Add a plugin instance to be loaded with the Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val myPlugin = MyCapacitorPlugin()
     * builder = builder.addPluginInstance(myPlugin)
     * ```
     *
     * Example usage (java):
     * ```java
     * val myPlugin = new MyCapacitorPlugin();
     * builder = builder.addPluginInstance(myPlugin);
     * ```
     *
     * @param plugin the plugin instance to add to the portal
     * @return the instance of the PortalBuilder with the plugin instances added
     */
    fun addPluginInstance(plugin: Plugin): PortalBuilder {
        pluginInstances.add(plugin)
        return this
    }

    /**
     * Add an Asset Map to the Portal used with shared assets.
     *
     * Example usage (kotlin):
     * ```kotlin
     * builder = builder.addAssetMap(AssetMap("images","/shared/images","images"))
     * ```
     *
     * Example usage (java):
     * ```java
     * builder = builder.addAssetMap(new AssetMap("images","/shared/images","images"));
     * ```
     *
     * @param assetMap the Asset Map to add
     * @return the instance of the PortalBuilder with the Asset Map added
     */
    fun addAssetMap(assetMap: AssetMap): PortalBuilder {
        assetMaps.put(assetMap.getAssetPath(), assetMap)
        return this
    }

    /**
     * Sets the initial context to pass to the web view.
     * You can pass in either a [Map] or a [String] that will be parsed into a JSON object.
     *
     * Example usage with a [Map] (kotlin):
     * ```kotlin
     * val map: Map<String, Any> = mapOf(
     *     "foo" to "bar",
     *     "ionic" to "portals"
     *     "num" to 42
     * )
     *
     * builder = builder.setInitialContext(map)
     * ```
     *
     * Example usage with a [String] (kotlin):
     * ```kotlin
     * builder = builder.setInitialContext("{\"foo\": \"bar\"}")
     * ```
     *
     * Example usage with a [Map] (java):
     * ```java
     * Map<String, Object> map = Map.ofEntries(
     *     new AbstractMap.SimpleEntry<String, @NotNull Object>("foo", "bar"),
     *     new AbstractMap.SimpleEntry<String, @NotNull Object>("ionic", "portals"),
     *     new AbstractMap.SimpleEntry<String, @NotNull Object>("num", 42)
     * );
     *
     * builder = builder.setInitialContext(map);
     * ```
     *
     * Example usage with a [String] (java):
     * ```java
     * builder = builder.setInitialContext("{\"foo\": \"bar\"}");
     * ```
     *
     * @param initialContext the initial context to add to the Portal
     * @return the instance of the PortalBuilder with the initial context set
     */
    fun setInitialContext(initialContext: Any): PortalBuilder {
        this.initialContext = initialContext
        return this
    }

    /**
     * Set a list of Capacitor [Plugin] to be loaded with the Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val list: MutableList<Class<out Plugin?}>> = mutableListOf(
     *     FooPlugin::class.java,
     *     BarPlugin::class.java,
     *     BazPlugin::class.java
     * )
     *
     * builder = builder.setPlugins(list)
     * ```
     *
     * Example usage (java):
     * ```java
     * List<? extends Plugin> list = Array.asList(
     *     FooPlugin.class,
     *     BarPlugin.class,
     *     BazPlugin.class
     * );
     *
     * builder = builder.setPlugins(list);
     * ```
     *
     * @param plugins a list of plugins to be used with the Portal
     * @return the instance of the PortalBuilder with the plugins set
     */
    fun setPlugins(plugins: MutableList<Class<out Plugin?>>): PortalBuilder {
        this.plugins = plugins
        return this
    }

    /**
     * Set a list of [AssetMap] to the Portal used with shared assets.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val assetMaps = LinkedHashMap<String, AssetMap>();
     * assetMaps["images"] = AssetMap("images", "/shared/images", "images")
     * builder = builder.setAssetMaps(assetMaps)
     * ```
     *
     * Example usage (java):
     * ```java
     * LinkedHashMap<String, AssetMap> assetMaps = new LinkedHashMap<String, AssetMap>();
     * assetMaps.put("images", new AssetMap("images","/shared/images","images"));
     * builder = builder.setAssetMaps(assetMaps);
     * ```
     *
     * @param assetMaps
     * @return the instance of the PortalBuilder with the asset maps set
     */
    fun setAssetMaps(assetMaps: LinkedHashMap<String, AssetMap>): PortalBuilder {
        this.assetMaps = assetMaps
        return this
    }

    /**
     * Set the [PortalFragment] class used with displaying the Portal when added to an XML layout.
     *
     * Example usage (kotlin):
     * ```kotlin
     * builder = builder.setPortalFragmentType(MyPortalFragment::class.java)
     * ```
     *
     * Example usage (java):
     * ```java
     * builder = builder.setPortalFragmentType(MyPortalFragment.class);
     * ```
     *
     * @param portalFragmentType a class that extends [PortalFragment]
     * @return the instance of the PortalBuilder with the fragment type set
     */
    fun setPortalFragmentType(portalFragmentType: Class<out PortalFragment?>): PortalBuilder {
        this.portalFragmentType = portalFragmentType
        return this
    }

    /**
     * Set the [LiveUpdate] config if using the Live Updates SDK with Portals.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val liveUpdateConfig = LiveUpdate("appId", "production")
     * builder = builder.setLiveUpdateConfig(liveUpdateConfig)
     * ```
     *
     * Example usage (java):
     * ```java
     * LiveUpdate liveUpdateConfig = new LiveUpdate("appId", "production");
     * builder = builder.setLiveUpdateConfig(liveUpdateConfig);
     * ```
     *
     * @param context the Android [Context] used with Live Update configuration
     * @param liveUpdateConfig the Live Update config object
     * @param updateOnAppLoad if a Live Update sync should occur as soon as the Portal loads
     * @return the instance of the PortalBuilder with the Live Update config set
     */
    @JvmOverloads
    fun setLiveUpdateConfig(context: Context, liveUpdateConfig: LiveUpdate, updateOnAppLoad: Boolean = true): PortalBuilder {
        this.liveUpdateConfig = liveUpdateConfig
        if(liveUpdateConfig.assetPath == null) {
            liveUpdateConfig.assetPath = this._startDir ?: this.name
        }

        LiveUpdateManager.initialize(context)
        LiveUpdateManager.cleanVersions(context, liveUpdateConfig.appId)
        LiveUpdateManager.addLiveUpdateInstance(context, liveUpdateConfig)
        if (updateOnAppLoad) {
            LiveUpdateManager.sync(context, arrayOf(liveUpdateConfig.appId))
        }
        return this
    }

    /**
     * Creates the [Portal] instance from the current state of the [PortalBuilder] provided.
     * This finishes building the Portal.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val portal: Portal = builder.create()
     * ```
     *
     * Example usage (java):
     * ```java
     * Portal portal = builder.create();
     * ```
     *
     * @return a built Portal instance
     */
    fun create(): Portal {
        val portal = Portal(name)
        portal.startDir = this._startDir ?: this.name
        portal.addPlugins(plugins)
        portal.addPluginInstances(pluginInstances)
        portal.addAssetMaps(assetMaps)
        portal.initialContext = this.initialContext
        portal.portalFragmentType = this.portalFragmentType
        portal.liveUpdateConfig = this.liveUpdateConfig
        onCreate(portal)
        return portal
    }

}
