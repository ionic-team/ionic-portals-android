package io.ionic.portals

import android.util.Log
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class PortalsPubSub {
    private var subscriptions = ConcurrentHashMap<String, MutableMap<Int, (data: SubscriptionResult) -> Unit>>()
    private var subscriptionRef = AtomicInteger(0)

    fun publish(topic: String, data: Any?) {
        subscriptions[topic]?.let {
            for ((ref, listener) in it) {
                val result = SubscriptionResult(topic, data, ref)
                listener(result)
            }
        }
    }

    fun subscribe(topic: String, callback: (result: SubscriptionResult) -> Unit): Int {
        val ref = subscriptionRef.incrementAndGet()
        subscriptions[topic]?.let { subscription ->
            subscription[ref] = callback
        } ?: run {
            val subscription = mutableMapOf(ref to callback)
            subscriptions[topic] = subscription
        }
        return ref
    }

    fun unsubscribe(topic: String, subscriptionRef: Int) {
        subscriptions[topic]?.remove(subscriptionRef)
    }

    companion object {
        @JvmStatic
        val shared = PortalsPubSub()
    }
}

@CapacitorPlugin(name = "Portals")
class PortalsPlugin(private val pubSub: PortalsPubSub = PortalsPubSub.shared) : Plugin() {
    private val subscriptionRefs = ConcurrentHashMap<String, Int>()

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

        pubSub.publish(topic, data)
        call.resolve()
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    override fun addListener(call: PluginCall?) {
        super.addListener(call)
        val topic = call?.getString("eventName") ?: return
        if (subscriptionRefs[topic] != null)  { return }
        val ref = pubSub.subscribe(topic) { result ->
            notifyListeners(topic, result.toJSObject())
        }

        subscriptionRefs[topic] = ref
    }

    override fun handleOnDestroy() {
        super.handleOnDestroy()
        for ((key, ref) in subscriptionRefs) {
            pubSub.unsubscribe(key, ref)
        }
    }
}

fun JSONObject.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>();
    this.keys().forEach {
        map[it] = this.get(it)
    }
    return map
}

data class SubscriptionResult(val topic: String, val data: Any?, val subscriptionRef: Int) {
    fun toJSObject(): JSObject {
        val jsObject = JSObject()
        jsObject.put("topic", this.topic)
        jsObject.put("data", this.data)
        return jsObject
    }
}
