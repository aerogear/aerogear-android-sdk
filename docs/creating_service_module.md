## Creating a new Service Module project in this repository

This section walks through adding a new Service Module project to the SDK repository.

### Creating the project

New Service Modules in this repository should be created under the `org.aerogear.mobile.*` namespace.

See the [`keycloak-service-module`](../keycloak-service-module/build.gradle) for examples.

1. Via Android Studio

    1. Create a new Android Library model by navigating to `File > New > New Module...` and selecting the `Android Library` option.

    2. Input the new module name and ensure it is created under the `org.aerogear.mobile.*` namespace. Ignore the value of the `Minimum SDK` field for now.

    3. After the module is created, update the fields in the generated `build.gradle` file to target the corresponding constants in the [`constants.gradle`](../constants.gradle) file. 


2. Manually

Create a new folder in the root of the repository with the `/*-module` suffix, with a `build.gradle` and `gradle.properties` file describing it.

The `compileSdkVersion`, `minSdkVersion` and `targetSdkVersion` must target the corresponding constants in the [`constants.gradle`](../constants.gradle) file.

Following that, add the new module to the root [`settings.gradle`](../settings.gradle).

### Adding the Bill of Materials from the SDK

Service modules should preferably utilize dependencies defined in the `aerogear-android-sdk-bom`, see [Using the Android SDK BOM in your own projects](./using_the_android_sdk_bom.md) for details.
