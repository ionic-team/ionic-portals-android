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
                // If Live Updates is configured and has been updated, route to that location instead
                if (assetMap.liveUpdateConfig != null) {
                    val liveUpdateDir = LiveUpdateManager.getLatestAppDirectory(context, assetMap.liveUpdateConfig.appId)
                    if (liveUpdateDir != null) {
                        processedRoute.path =  "$liveUpdateDir.path/$path"
                        processedRoute.isAsset = false
                        return processedRoute
                    }
                }

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