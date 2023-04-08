package io.ionic.portals

import android.content.Context
import com.getcapacitor.ProcessedRoute
import com.getcapacitor.RouteProcessor

/**
 * A class used by [PortalFragment] to provide instructions to the Capacitor router to load shared assets.
 *
 * @property context an Android [Context] used in the routing process
 * @property assetMaps a set of [AssetMap] objects describing how to route to shared assets
 */
class PortalsRouteProcessor(val context: Context, val assetMaps: LinkedHashMap<String, AssetMap>): RouteProcessor {
    /**
     * A callback used by Capacitor to intercept routing logic to route to shared assets.
     *
     * @param basePath the base URL for the path
     * @param path the remaining path string
     * @return the [ProcessedRoute] to be used by Capacitor to route with
     */
    override fun process(basePath: String?, path: String?): ProcessedRoute {
        val processedRoute = ProcessedRoute()

        // If AssetMap contains this virtual route, reroute to shared assets location
        assetLoop@ for ((mapName, assetMap) in assetMaps) {
            if (path != null) {
                if(path.startsWith(assetMap.virtualPath)) {
                    val assetMapObj = assetMaps[mapName]
                    if (assetMapObj != null) {
                        var trimmedPath = path.replace(assetMap.virtualPath,assetMapObj.getAssetPath())
                        if (trimmedPath.startsWith("/")) {
                            trimmedPath = trimmedPath.drop(1)
                        }

                        processedRoute.path = trimmedPath
                        processedRoute.isAsset = true
                        processedRoute.isIgnoreAssetPath = true
                        break@assetLoop
                    }
                }
            }
        }

        if (processedRoute.path == null || processedRoute.path.isEmpty()) {
            processedRoute.path = "$basePath$path"
            processedRoute.isAsset = true
        }

        return processedRoute
    }
}