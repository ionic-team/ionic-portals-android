package io.ionic.portals

import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

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
    @JvmStatic
    private var registered: Boolean = false
    @JvmStatic
    private var unregisteredMessageShown: Boolean = false
    @JvmStatic
    private var registeredError: Boolean = false

    /**
     * Adds a Portal to the Portal Manager. This is not necessary if the Portal is created using
     * the [newPortal] function.
     *
     * @param portal The Portal to add
     */
    @JvmStatic
    fun addPortal(portal: Portal) {
        portals[portal.name] = portal

        if (!registered && !unregisteredMessageShown) {
            displayUnregisteredMessage()
        }
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
     * @throws NoSuchElementException throws this exception if the Portal does not exist
     */
    @JvmStatic
    fun getPortal(name: String): Portal {
        if (registeredError) {
            registrationError()
        }

        return portals[name] ?: throw IllegalStateException("Portal with portalId $name not found in PortalManager")
    }

    /**
     * Removes the Portal from the Portal Manager. The Portal will be returned if it was present. If not, null is returned.
     * Note: if the Portal uses Live Updates and registered an instance on creation, the Live Update instance for the app
     * is not removed.
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
     * Validate this copy of Portals with an API key. This function works offline and only needs to
     * be run once before creating your first [Portal].
     *
     * Example usage (kotlin):
     * ```kotlin
     * PortalManager.register("YOUR_PORTALS_KEY")
     * ```
     *
     * Example usage (java):
     * ```java
     * PortalManager.register("YOUR_PORTALS_KEY");
     * ```
     *
     * @param key The key for Portals provided by the Ionic dashboard
     */
    @JvmStatic
    fun register(key: String) {
        registered = verify(key)
    }

    /**
     * Check if Portals has been successfully registered with a valid key.
     *
     * @return true if Portals is successfully registered
     */
    @JvmStatic
    fun isRegistered(): Boolean {
        return registered
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

    /**
     * Verifies the provided registration key string against the Portals public key.
     *
     * @param key: The Portals registration key to validate
     * @return True if validation was successful, false if not.
     */
    private fun verify(key: String): Boolean {
        val jwtDelimiter = '.'
        val PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1+gMC3aJVGX4ha5asmEF" +
            "TfP0FTFQlCD8d/J+dhp5dpx3ErqSReru0QSUaCRCEGV/ZK3Vp5lnv1cREQDG5H/t" +
            "Xm9Ao06b0QJYtsYhcPgRUU9awDI7jRKueXyAq4zAx0RHZlmOsTf/cNwRnmRnkyJP" +
            "a21mLNClmdPlhWjS6AHjaYe79ieAsftFA+QodtzoCo+w9A9YCvc6ngGOFoLIIbzs" +
            "jv6h9ES27mi5BUqhoHsetS4u3/pCbsV2U3z255gtjANtdIX/c5inepLuAjyc1aPz" +
            "2eu4TbzabvJnmNStje82NW36Qij1mupc4e7dYaq0aMNQyHSWk1/CuIcqEYlnK1mb" +
            "kQIDAQAB"

        try {
            val publicBytes: ByteArray = Base64.decode(PUBLIC_KEY, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val pubKey: PublicKey = keyFactory.generatePublic(keySpec)

            val parts = key.trim().split(jwtDelimiter)
            return if (parts.size == 3) {
                val header = parts[0].toByteArray(Charsets.UTF_8)
                val payload = parts[1].toByteArray(Charsets.UTF_8)
                val tokenSignature = Base64.decode(parts[2], Base64.URL_SAFE)

                val rsaSignature = Signature.getInstance("SHA256withRSA")
                rsaSignature.initVerify(pubKey)
                rsaSignature.update(header)
                rsaSignature.update(jwtDelimiter.toByte())
                rsaSignature.update(payload)

                val result = rsaSignature.verify(tokenSignature)
                if (!result) {
                    registrationError()
                }

                result
            } else {
                registrationError()
                false
            }
        } catch (e: Exception) {
            registrationError()
        }

        return false
    }

    /**
     * Display an error log to warn the developer that Portals is unregistered.
     */
    private fun displayUnregisteredMessage() {
        unregisteredMessageShown = true
        Log.e("Portals", "Don't forget to register your copy of portals! Register at: ionic.io/register-portals")
    }

    /**
     * Display an error log to warn the developer that Portals registration failed.
     */
    private fun registrationError() {
        registeredError = true
        Log.e("Portals", "Error validating your key for Ionic Portals. Check your key and try again.")
    }

    /**
     * Check if there is a Portals registration issue.
     *
     * @return true if there is a Portals registration error
     */
    internal fun isRegisteredError(): Boolean {
        return registeredError
    }
}