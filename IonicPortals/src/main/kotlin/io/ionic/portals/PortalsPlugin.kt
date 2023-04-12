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

    /**
     * Publish a message to registered native callbacks.
     *
     * @param topic the topic name for the message
     * @param data the message data
     */
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

    /**
     * Unsubscribes from a topic
     *
     * @param topic the name of the topic to unsubscribe from
     * @param subscriptionRef the subscription reference returned from [subscribe]
     */
    fun unsubscribe(topic: String, subscriptionRef: Int) {
        subscriptions[topic]?.remove(subscriptionRef)
    }

    companion object {
        /**
         * The default shared [PortalsPubSub] instance.
         */
        @JvmStatic
        val shared = PortalsPubSub()
    }
}

/**
 * A special Capacitor Plugin within the Portals library that allows for bi-directional communication
 * between Android and web code. It is loaded with every Portal automatically and does not need to be
 * added like other plugins if the default behavior is desired.
 *
 * If events should be scoped to a specific Portal or group of Portals, this should be initialized
 * with an instance of [PortalsPubSub] and added to [Portal.pluginInstances] via
 * [PortalBuilder.addPluginInstance].
 */
@CapacitorPlugin(name = "Portals")
class PortalsPlugin(private val pubSub: PortalsPubSub = PortalsPubSub.shared) : Plugin() {
    private val subscriptionRefs = ConcurrentHashMap<String, Int>()

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

        pubSub.publish(topic, data)
        call.resolve()
    }

    /**
     * Allows the web to subscribe to messages from native.
     *
     * @param call the [PluginCall] from web to native
     */
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
        return jsObject
    }
}
