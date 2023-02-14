package io.ionic.portals

class AssetMap(
    val name: String,
    val virtualPath: String,
    val prefix: String = "/assets",
    val path: String,
    val startDir: String
) {
    val assetPath: String = "\$prefix\$path"

}