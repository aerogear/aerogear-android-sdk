## Android SDK Overview and Architecture

This section describes the structure of this repository.

### Core SDK

Present in the [`core/`](core/) folder, the Core Android SDK provides the basic mechanisms for interacting with services, such as interfaces for network requests, depending on other services, managing configuration and secrets, error handling, etc.

#### Service Registry

The Core of the SDK also provides a global [`ServiceModuleRegistry`](core/src/main/java/org/aerogear/mobile/core/ServiceModuleRegistry.java) that contains references to the available service modules, their configuration values and implementations.

It is through this registry that services can access other service modules that they depend upon.

### Service Modules

Service Modules provide the necessary implementation for consuming a particular service, it must implement the [`org.aerogear.mobile.core.ServiceModule`](core/src/main/java/org/aerogear/mobile/core/ServiceModule.java) interface in order to supply a `bootstrap` function that is invoked by the Core in order to inject dependencies and configuration.

The bootstrapping action also allows the service module to use the aforementioned facilites from Core.

## Creating a new Service Module project in this repository

In order to create a new Service Module in this repository, create a start a new folder in its root with the `/*-module` suffix, with a `build.gradle` and `gradle.properties` file describing it.

Following that, add the new module to the root [`settings.gradle`](settings.gradle).

See the [`keycloak-service-module`](./keycloak-service-module/build.gradle) for examples.

Service modules should preferably utilize dependencies defined in the `aerogear-android-sdk-bom`, see [Using the Android SDK BOM in your own projects](docs/using_the_android_sdk_bom.md) for details.
