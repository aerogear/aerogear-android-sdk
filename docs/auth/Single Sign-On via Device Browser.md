## Single Sign-On (SSO) via the Device Browser
The Auth SDK uses OpenID [AppAuth library](https://github.com/openid/AppAuth-Android) and implements OpenID's [auhtorization code flow](http://openid.net/specs/openid-connect-core-1_0.html#CodeFlowAuth) to achieve SSO via the device browser.

### Prerequisite's
1. Ensure your app is using the Android SDK - [Getting Started with AeroGear Services Android SDK](https://github.com/aerogear/aerogear-android-sdk/blob/master/docs/getting-started.adoc) and add in the Auth SDK dependency - [AeroGear Services Auth SDD](https://github.com/aerogear/aerogear-android-sdk/blob/master/docs/auth/README.adoc)
2. Ensure that keycloak is preconfigured to manage authentication and access control for your service - [Keycloak Client Registration](http://www.keycloak.org/docs/3.2/securing_apps/topics/client-registration.html)

### Steps to achieve SSO via device browser
Let's assume that you have two apps, **Email App** and **Messaging App**.  Both apps have a service, Service A, that can only be accessed by authenticated users.  The authentication and access control to Service A should already be preconfigured in Keycloak (see prerequisite's).

#### The following steps will go through SSO via an end-user's device browser.

1. An end-user, Bob, tries to access Service A within **Email App** 

   **Email App** should have a function defined that is invoked when Bob tries to access Service A, let's call it `loginToServiceA()`. This `loginToServiceA()` function, must invoke the Auth SDK method `AuthService.login(final ICredential credentials)` (I will provide a link back to the SDK docs for detailed info on how to login using the SDK).

2. On invoking the Auth SDK function `AuthService.login(final ICredential credentials)`, the device browser on Bob's device pops up with the Keycloak login web page.

3. Bob can now enter his username and password into the Keycloak login page.

4. On clicking login in the Keycloak login page, the Auth SDK is now handling the authentication of Bob for Service A.

5. On successful login, Bob is authenticated on **Email App**  to access Service A.

Let's assume that **Messaging App** also uses the Auth SDK in the same way as the **Email App** described in step 1.

Now let's say Bob tries to access Service A from **Messaging App**.  As Bob has already been authenticated via his device browser for Service A before, he is automatically granted access to Service A within the **Messaging App**.