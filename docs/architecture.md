## Android SDK Overview and Architecture

This section describes the structure of this repository.

### Core SDK

Present in the [`core/`](../core/) folder, the Core Android SDK provides the basic mechanisms for interacting with services, such as interfaces for network requests, depending on other services, managing configuration and secrets, error handling, etc.

#### Service Registry

The Core of the SDK also provides a global [`ServiceModuleRegistry`](../core/src/main/java/org/aerogear/mobile/core/ServiceModuleRegistry.java) that contains references to the available service modules, their configuration values and implementations.

It is through this registry that services can access other service modules that they depend upon.

### Service Modules

Service Modules provide the necessary implementation for consuming a particular service, it must implement the [`org.aerogear.mobile.core.ServiceModule`](../core/src/main/java/org/aerogear/mobile/core/ServiceModule.java) interface in order to supply a `bootstrap` function that is invoked by the Core in order to inject dependencies and configuration.

The bootstrapping action also allows the service module to use the aforementioned facilites from Core.

### Creating a new Service Module

See [Creating a new Service Module in this repository](./creating_service_module.md)
