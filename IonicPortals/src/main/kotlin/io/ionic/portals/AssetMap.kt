package io.ionic.portals

class AssetMap(
    // A name for the asset map.
    val name: String,
    // A path to match via the web.
    val virtualPath: String,
    // The path of the shared assets within the Android assets directory.
    val path: String
) {
    fun getAssetPath(): String {
        return path
    }
}