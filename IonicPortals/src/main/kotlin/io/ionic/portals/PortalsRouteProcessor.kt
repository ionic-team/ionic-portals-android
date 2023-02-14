package io.ionic.portals

import android.content.Context
import com.getcapacitor.ProcessedRoute
import com.getcapacitor.RouteProcessor
import io.ionic.liveupdates.LiveUpdateManager


class PortalsRouteProcessor(val context: Context, val assetMaps: LinkedHashMap<String, AssetMap>): RouteProcessor {
    override fun process(basePath: String?, path: String?): ProcessedRoute {
        val processedRoute = ProcessedRoute()

        // If AssetMap contains this file, reroute to shared assets
        if (assetMaps.containsKey(path)) {
            val assetMap = assetMaps[path]
            if (assetMap != null) {
                processedRoute.path = assetMap.assetPath
                processedRoute.isAsset = true
            }
        } else {
            processedRoute.path = "$basePath/$path"
            processedRoute.isAsset = true
        }

        return processedRoute
    }
}