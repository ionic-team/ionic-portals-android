package io.ionic.portals

import io.ionic.liveupdates.LiveUpdate

class AssetMap(
    val name: String,
    val virtualPath: String,
    val prefix: String = "/assets",
    val path: String,
    val startDir: String,
    val liveUpdateConfig: LiveUpdate? = null
) {
    val assetPath: String = "\$prefix\$path"

}