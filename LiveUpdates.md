# Live Updates

Portals can load updated web assets from Ionic Live Updates or from an external live update provider. Each Portal has one live update source, represented by `Portal.liveUpdateProvider`.

## Ionic Live Updates

Use Ionic Live Updates with the existing `setLiveUpdateConfig` builder API.

```kotlin
val liveUpdateConfig = LiveUpdate("appId", "production")

PortalManager.newPortal("help")
    .setStartDir("webapp")
    .setLiveUpdateConfig(context, liveUpdateConfig)
    .create()
```

```java
LiveUpdate liveUpdateConfig = new LiveUpdate("appId", "production");

PortalManager.newPortal("help")
    .setStartDir("webapp")
    .setLiveUpdateConfig(context, liveUpdateConfig)
    .create();
```

`setLiveUpdateConfig` configures the Portal with `Portal.LiveUpdateProvider.Ionic(liveUpdateConfig)`, initializes Ionic Live Updates, registers the live update instance, and optionally starts an Ionic Live Updates sync.

Manual Ionic Live Updates syncs should continue to use the Ionic Live Updates SDK.

```java
LiveUpdateManager.sync(context, new String[] { "appId" });
```

## External Providers

Use an external provider by passing a `LiveUpdateProviderManager`.

```kotlin
val providerManager: LiveUpdateProviderManager = getProviderManager()

val helpPortal = PortalManager.newPortal("help")
    .setStartDir("webapp")
    .setLiveUpdateProviderManager(providerManager, updateOnAppLoad = false)
    .create()

helpPortal.syncProvider(callback)
```

```java
LiveUpdateProviderManager providerManager = getProviderManager();

Portal helpPortal = PortalManager.newPortal("help")
    .setStartDir("webapp")
    .setLiveUpdateProviderManager(providerManager, false)
    .create();

helpPortal.syncProvider(callback);
```

`setLiveUpdateProviderManager` configures the Portal with `Portal.LiveUpdateProvider.Provider(providerManager)`. Android Portals does not use a provider registry; the app creates the provider manager and passes that instance to the Portal.

After a provider sync completes, reload the Portal view or fragment to load the synced assets.

```java
helpPortal.syncProvider(callback);
portalFragment.reload();
```

## Migration Notes

`Portal.liveUpdateConfig` has been replaced by `Portal.liveUpdateProvider`.

Builder-based Ionic Live Updates setup still uses `setLiveUpdateConfig`.

```java
PortalManager.newPortal("help")
    .setLiveUpdateConfig(context, liveUpdateConfig)
    .create();
```

Direct `Portal` property usage should wrap the config in the Ionic live update provider case.

```kotlin
portal.liveUpdateProvider = Portal.LiveUpdateProvider.Ionic(liveUpdateConfig)
```

External providers should use the provider case.

```kotlin
portal.liveUpdateProvider = Portal.LiveUpdateProvider.Provider(providerManager)
```
