# Consumer rules applied to apps that use Ionic Portals.

# Live Updates is an optional compileOnly dependency.
-dontwarn io.ionic.liveupdates.**

# linkMessageReceivers finds @PortalMethod functions with Kotlin reflection
# and uses the function name as the topic, so keep them and their names.
-keepattributes RuntimeVisibleAnnotations
-keep @interface io.ionic.portals.PortalMethod
-keep class kotlin.Metadata { *; }
-keepclasseswithmembers class * {
    @io.ionic.portals.PortalMethod <methods>;
}
