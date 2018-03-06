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
[aerogear google group](https://groups.google.com/forum/#!forum/aerogear)
-- both are actively monitored.

# Developing the Android SDK

## Prerequisites

Ensure you have the following installed in your machine:

- [Java Development Kit](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
- [Android Studio and Android SDK](https://developer.android.com/studio/index.html)
- [Git SCM](http://git-scm.com/)

## Cloning the repository

```bash
git clone https://github.com/aerogear/aerogear-android-sdk.git
cd aerogear-android-sdk/
```

## Installing dependencies and building the SDK

Run the following command to install the dependencies for the Android SDK:

```bash
./gradlew install
```

In Android Studio, go to `Tools > Android > Sync Project With Gradle Files`.

See the [Gradle Documentation](https://docs.gradle.org/current/userguide/pt02.html) for more information on Gradle and the Gradle Wrapper

## Adding Our Style Guide to Android Studio

We are using spotless to enforce our style guidelines on the project.  You may invoke spotless manually by running `./gradlew spotlessCheck`.  If there are errors spotless will prompt you to run `./gradlew spotlessApply` and auto fix them.  If your code fails to meet the style guidelines it will fail.  However, if you are using Android Studio you may make use of the [Eclipse Code Formatter](http://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter).  You can find our Eclipse formatting settings in [codequality/eclipse-code-style.xml](./codequality/eclipse-code-style.xml) and out import orders in [codequality/aerogear.importorder](codequality/aerogear.importorder).
