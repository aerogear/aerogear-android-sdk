# Contributing to the AeroGear Android SDK

The AeroGear Android SDK is part of the [AeroGear project](https://aerogear.org/), see the [Community Page](https://aerogear.org/community) for general guidelines for contributing to the project.

This document details specifics for contributions to the Android SDK.

## Issue tracker

The tracking of issues for the AeroGear Android SDK is done in the [AeroGear Android Project](https://issues.jboss.org/projects/AGDROID/issues) in the [JBoss Developer JIRA](https://issues.jboss.org).

See the [AeroGear JIRA Usage and Guidelines Guide](https://aerogear.org/docs/guides/JIRAUsage/) for information on how the issue tracker relates to contributions to this project.

## Asking for help

Whether you're contributing a new feature or bug fix, or simply submitting a
ticket, the Aerogear team is available for technical advice or feedback. 
You can reach us at [#aerogear](ircs://chat.freenode.net:6697/aerogear) on [Freenode IRC](https://freenode.net/) or the 
[aerogear-dev list](http://lists.jboss.org/pipermail/aerogear-dev/)
-- both are actively monitored.

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
