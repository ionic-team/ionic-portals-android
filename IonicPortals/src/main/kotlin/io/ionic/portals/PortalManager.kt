package io.ionic.portals

/**
 * A class used to create and manage [Portal] instances. It follows a [Singleton Pattern](https://en.wikipedia.org/wiki/Singleton_pattern)
 * to allow access to any [Portal](./portal) from anywhere in the application.
 *
 * Example usage (kotlin):
 * ```kotlin
 * PortalManager.newPortal("my_portal")
 *     .addPlugin(MyCapacitorPlugin::class.java)
 *     .setPortalFragmentType(MyFadeInOutPortalFragment::class.java)
 *     .setInitialContext(mapOf("myVariableFromAndroid" to 42))
 *     .setStartDir("web_app")
 *     .create()
 * ```
 *
 * Example usage (java):
 * ```java
 * PortalManager.newPortal("my_portal")
 *     .addPlugin(MyCapacitorPlugin.class)
 *     .setPortalFragmentType(MyFadeInOutPortalFragment.class)
 *     .setInitialContext(Map.of("myVariableFromAndroid", 42))
 *     .setStartDir("web_app")
 *     .create();
 * ```
 */
object PortalManager {
    @JvmStatic
    private val portals: MutableMap<String, Portal> = mutableMapOf()

    /**
     * Adds a Portal to the Portal Manager. This is not necessary if the Portal is created using
     * the [newPortal] function.
     *
     * @param portal The Portal to add
     */
    @JvmStatic
    fun addPortal(portal: Portal) {
        portals[portal.name] = portal
    }

    /**
     * Returns a [Portal] object given the name of the portal. If the portal does not exist, an exception is thrown.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val portal: Portal = PortalManager.getPortal("my_portal")
     * ```
     *
     * Example usage (java):
     * ```java
     * Portal portal = PortalManager.getPortal("my_portal");
     * ```
     *
     * @param name the portal name
     * @throws IllegalStateException throws this exception if the Portal does not exist
     */
    @JvmStatic
    fun getPortal(name: String): Portal {
        return portals[name] ?: throw IllegalStateException("Portal with portalId $name not found in PortalManager")
    }

    /**
     * Removes the Portal from the Portal Manager. The Portal will be returned if it was present. If not, null is returned.
     * Note: removing a Portal does not remove its Ionic Live Updates app instance from the
     * Ionic Live Updates manager.
     *
     * @param name the name of the Portal to remove
     */
    @JvmStatic
    fun removePortal(name: String): Portal? {
        return portals.remove(name)
    }

    /**
     * Get the number of Portals managed by the Portal Manager.
     *
     * Example usage (kotlin):
     * ```kotlin
     * val portalCount: Int = PortalManager.size()
     * ```
     *
     * Example usage (java):
     * ```java
     * int portalCount = PortalManager.size();
     * ```
     *
     * @return how many Portals are managed by the Portal Manager
     */
    @JvmStatic
    fun size(): Int {
        return portals.size
    }

    /**
     * A helper function to build portal classes and add them to the manager.
     * Classes built with newPortal are added to the PortalManager automatically.
     *
     * Example usage (kotlin):
     *```kotlin
     * val builder: PortalBuilder = PortalManager.newPortal("my_portal")
     * val portal: Portal = builder.create()
     * ```
     *
     * Example usage (java):
     * ```java
     * PortalBuilder builder = PortalManager.newPortal("my_portal");
     * Portal portal = builder.create();
     * ```
     *
     * @param name the Portal name
     * @return a [PortalBuilder] object that has a fluent API to construct a Portal
     */
    @JvmStatic
    fun newPortal(name: String): PortalBuilder {
        return PortalBuilder(name, fun(portal) {
            this.addPortal(portal)
        })
    }
}
