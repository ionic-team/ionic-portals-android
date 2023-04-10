package io.ionic.portals

/**
 * An Annotation class used to create message receiver functions with the [PortalsPlugin].
 * A function annotated with this class can be triggered by using the `Portals.sendMessage()`
 * with the `message` parameter on the web code matching the function name in the native code.
 *
 * Example usage:
 * ```kotlin
 * class MyPortalFragment : PortalFragment() {
 *     // ...
 *     public fun onCreate(savedInstanceState: Bundle?): Unit {
 *         super.onCreate(savedInstanceState)
 *         // ...
 *         this.linkMessageReceivers(this)
 *     }
 *
 *     @PortalMethod
 *     public fun nativeFunction(payload: String): Unit {
 *         // run native code here
 *     }
 * }
 * ```
 *
 * ```java
 * class MyPortalFragment extends PortalFragment {
 *     // ...
 *     public void onCreate(@Nullable Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         // ...
 *         this.linkMessageReceivers(this);
 *     }
 *
 *     @PortalMethod
 *     public void nativeFunction(String payload) {
 *         // run native code here
 *     }
 * }
 * ```
 *
 * ```typescript
 * import { Portals } from "@native-portal/portals";
 *
 * Portals.sendMessage({ message: "nativeFunction", payload: result });
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class PortalMethod(val topic: String = "")