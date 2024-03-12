package io.ionic.portals

import android.annotation.SuppressLint
import android.content.Context
import com.getcapacitor.CapConfig
import com.getcapacitor.Logger

object DevConfiguration {

    @SuppressLint("PrivateApi")
    fun getServerUrl(context: Context, portalName: String): String? {
        val portalDirName = "$portalName.debug"
        val generalDirName = "portal.debug"
        val urlFileName = "url"

        val assetManager = context.assets
        var serverUrl = try {
            assetManager.open("$portalDirName/$urlFileName").bufferedReader().use {
                it.readText()
            }
        } catch (e: Exception) {
            null
        }

        if (serverUrl == null) {
            serverUrl = try {
                assetManager.open("$generalDirName/$urlFileName").bufferedReader().use {
                    it.readText()
                }
            } catch (e: Exception) {
                null
            }
        }

        return serverUrl
    }

    @SuppressLint("PrivateApi")
    fun getCapacitorConfig(context: Context, portalName: String): CapConfig? {
        val portalDirName = "$portalName.debug"
        val generalDirName = "portal.debug"
        val capConfigFileName = "capacitor.config.json"

        var serverConfig = try {
            val configFile = context.assets.open("$portalDirName/$capConfigFileName")
            CapConfig.loadFromAssets(context, portalDirName)
        } catch (e: Exception) {
            null
        }

        if (serverConfig == null) {
            serverConfig = try {
                val configFile = context.assets.open("$generalDirName/$capConfigFileName")
                CapConfig.loadFromAssets(context, generalDirName)
            } catch (e: Exception) {
                null
            }
        }

        return serverConfig
    }
}