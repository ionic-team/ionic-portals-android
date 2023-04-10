package io.ionic.portals

/**
 * A class representing a collection of assets to be used by one or more Portals.
 *
 * @property name a name for the asset map
 * @property virtualPath a path used to access shared assets from the web app
 * @property path the path of the shared assets within the Android assets directory
 */
class AssetMap(
    val name: String,
    val virtualPath: String,
    val path: String
) {
    /**
     * Get the path of the shared assets.
     *
     * @return the path of the shared assets from the Android assets directory
     */
    fun getAssetPath(): String {
        return path
    }
}