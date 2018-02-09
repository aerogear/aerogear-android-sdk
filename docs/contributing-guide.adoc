## Creating a new Service Module in this repository

This section walks through adding a new Service Module project to the SDK repository.

### Creating the project

New Service Modules in this repository should be created under the `org.aerogear.mobile.*` namespace.

See the [`keycloak-service-module`](../keycloak-service-module/build.gradle) for examples.

#### Via Android Studio

1. Create a new Android Library module by navigating to `File > New > New Module...` and selecting the `Android Library` option.

2. Input the new module name and ensure it is created under the `org.aerogear.mobile.*` namespace. Ignore the value of the `Minimum SDK` field for now.

3. After the module is created, update the fields in the generated `build.gradle` file to target the corresponding constants in the [`constants.gradle`](../constants.gradle) file:

```groovy
apply plugin: 'com.android.library'

android {
    compileSdkVersion project.ext.compileSdkVersion

    defaultConfig {
        minSdkVersion project.ext.minSdkVersion
        targetSdkVersion project.ext.targetSdkVersion
        versionCode 1
        versionName "1.0"
```

#### Manually/Via a terminal

1. Create a new folder in the root of the repository with the `/*-module` suffix, with a `build.gradle` and `gradle.properties` file describing it.

2. The `compileSdkVersion`, `minSdkVersion` and `targetSdkVersion` must target the corresponding constants in the [`constants.gradle`](../constants.gradle) file:

```groovy
apply plugin: 'com.android.library'

android {
    compileSdkVersion project.ext.compileSdkVersion

    defaultConfig {
        minSdkVersion project.ext.minSdkVersion
        targetSdkVersion project.ext.targetSdkVersion
        versionCode 1
        versionName "1.0"
```

3. Following that, add the new module to the root [`settings.gradle`](../settings.gradle).

### Adding the Bill of Materials from the SDK

Service modules should preferably utilize dependencies defined in the `aerogear-android-sdk-bom`, see [Using the Android SDK BOM in your own projects](./using_the_android_sdk_bom.md) for details.
