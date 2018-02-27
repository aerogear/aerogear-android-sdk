# Backports

This module is intended to be consumed by the Cordova and react-native SDKs. It provides native
implementations for features that can't be implemented otherwise.

## Why an extra module?

The current SDK modules (core, auth, ...) are difficult to wrap because they rely on Java8 features
and require a rather high minimum SDK version (too high for react-native).

The `backport` module does not depend on any other module. It's just a project with very low SDK
requirements that can easily be used by react native and Cordova. It contains the native pieces
for those SDKs.

## Why in the aerogear-android-sdk project?

That and the whole existence of this module are up for debate. It's just part of a POC for how the
Cordova and react-native SDKs can be implemented.
