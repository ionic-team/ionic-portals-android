package io.ionic.portals

import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import org.json.JSONObject

@CapacitorPlugin(name = "Portals")
class PortalsPlugin : Plugin() {

    companion object {
        @JvmStatic
        var subscriptions = mutableMapOf<String, MutableMap<Int, (data: SubscriptionResult) -> Unit>>()
        @JvmStatic
        var subscriptionRef = 0

        @JvmStatic
        fun publish(topic: String, data: Any) {
            subscriptions[topic]?.let {
                for((ref, listener) in it) {
                    val result = SubscriptionResult(topic, data, ref)
                    listener(result)
                }
            }
        }

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

        @JvmStatic
        fun unsubscribe(topic: String, subscriptionRef: Int) {
            subscriptions[topic]?.let { subscription ->
                subscription.remove(subscriptionRef)
            }
        }
    }

    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    fun publishNative(call: PluginCall) {
        val topic = call.getString("topic") ?: run {
            call.reject("topic not provided")
            return
        }
        val data = call.data.get("data");
        PortalsPlugin.publish(topic, data)
        call.resolve()
    }

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

fun JSONObject.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>();
    this.keys().forEach {
        map[it] = this.get(it)
    }
    return map
}

data class SubscriptionResult(
    val topic: String,
    val data: Any,
    val subscriptionRef: Int
) {
    fun toJSObject(): JSObject {
        val jsObject = JSObject()
        jsObject.put("topic", this.topic)
        jsObject.put("data", this.data)
        jsObject.put("subscriptionRef", this.subscriptionRef)
        return jsObject
    }
}
