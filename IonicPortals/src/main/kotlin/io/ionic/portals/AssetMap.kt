package io.ionic.portals

class AssetMap {
    val name: String
    val virtualPath: String

    val path: String

    var prefix: String = "/assets"
    var startDir: String? = null

    constructor(name: String, virtualPath: String, path: String) {
        this.name = name
        this.virtualPath = virtualPath
        this.path = path
    }

    constructor(name: String, virtualPath: String, path: String, startDir: String) {
        this.name = name
        this.virtualPath = virtualPath
        this.path = path
        this.startDir = startDir
    }

    constructor(name: String, virtualPath: String, path: String, startDir: String, prefix: String) {
        this.name = name
        this.virtualPath = virtualPath
        this.path = path
        this.prefix = prefix
        this.startDir = startDir
    }

    fun getAssetPath(): String {
        return path
    }
}