package io.ionic.portals

import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import org.json.JSONException
import org.json.JSONObject

/**
 * A special Capacitor Plugin within the Portals library that allows for bi-directional communication
 * between Android and web code. It is loaded with every Portal automatically and does not need to be
 * added like other plugins.
 */
@CapacitorPlugin(name = "Portals")
class PortalsPlugin : Plugin() {
    companion object {
        /**
         * The subscriptions registered with the plugin.
         */
        @JvmStatic
        var subscriptions = mutableMapOf<String, MutableMap<Int, (data: SubscriptionResult) -> Unit>>()

        /**
         * A reference ID for the subscription.
         */
        @JvmStatic
        var subscriptionRef = 0

        /**
         * Publish a message to registered native callbacks.
         *
         * @param topic the topic name for the message
         * @param data the message data
         */
        @JvmStatic
        fun publish(topic: String, data: Any?) {
            subscriptions[topic]?.let {
                for ((ref, listener) in it) {
                    val result = SubscriptionResult(topic, data, ref)
                    listener(result)
                }
            }
        }

        /**
         * Subscribe to a topic.
         *
         * @param topic the name of the topic to subscribe to
         * @param callback the callback to trigger when the subscription is called
         * @return the reference ID of the subscription
         */
        @JvmStatic
        fun subscribe(topic: String, callback: (result: SubscriptionResult) -> Unit): Int {
            subscriptionRef++
            subscriptions[topic]?.let { subscription ->
                subscription[subscriptionRef] = callback
            } ?: run {
                val subscription = mutableMapOf(subscriptionRef to callback)
                subscriptions[topic] = subscription
            }
            return subscriptionRef
        }

        /**
         *
         */
        @JvmStatic
        fun unsubscribe(topic: String, subscriptionRef: Int) {
            subscriptions[topic]?.remove(subscriptionRef)
        }
    }

    /**
     * Publishes a message from the web app to the native app.
     *
     * @param call the [PluginCall] from web to native
     */
    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    fun publishNative(call: PluginCall) {
        val topic = call.getString("topic") ?: run {
            call.reject("topic not provided")
            return
        }

        val data = try {
            call.data.get("data")
        } catch (e: JSONException) {
            null
        }

        PortalsPlugin.publish(topic, data)
        call.resolve()
    }

    /**
     * Allows the web to subscribe to messages from native.
     *
     * @param call the [PluginCall] from web to native
     */
    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    fun subscribeNative(call: PluginCall) {
        val topic = call .getString("topic") ?: run {
            call.reject("topic not provided")
            return
        }
        call.setKeepAlive(true)
        val ref = PortalsPlugin.subscribe(topic) { result ->
            call.resolve(result.toJSObject())
        }
        val result = JSObject()
        result.put("topic", topic)
        result.put("subscriptionRef", ref)
        call.resolve(result)
    }

    /**
     * Allows the web to unsubscribe from receiving messages from native.
     *
     * @param call the [PluginCall] from web to native
     */
    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    fun unsubscribeNative(call: PluginCall) {
        val topic = call .getString("topic") ?: run {
            call.reject("topic not provided")
            return
        }
        val subscriptionRef = call .getInt("subscriptionRef") ?: run {
            call.reject("subscriptionRef not provided")
            return
        }
        PortalsPlugin.unsubscribe(topic, subscriptionRef)
        call.resolve()
    }
}

/**
 * An extension function to convert a JSONObject to a map.
 *
 * @return a map representation of the JSONObject
 */
fun JSONObject.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>();
    this.keys().forEach {
        map[it] = this.get(it)
    }
    return map
}

/**
 * A class used for messages passed to subscriptions.
 *
 * @property topic the subscription topic
 * @property data the message data
 * @property subscriptionRef the subscription reference ID
 */
data class SubscriptionResult(val topic: String, val data: Any?, val subscriptionRef: Int) {
    /**
     * Serializes the SubscriptionResult to a JSObject.
     *
     * @return a JSObject representing the SubscriptionResult
     */
    fun toJSObject(): JSObject {
        val jsObject = JSObject()
        jsObject.put("topic", this.topic)
        jsObject.put("data", this.data)
        jsObject.put("subscriptionRef", this.subscriptionRef)
        return jsObject
    }
}
