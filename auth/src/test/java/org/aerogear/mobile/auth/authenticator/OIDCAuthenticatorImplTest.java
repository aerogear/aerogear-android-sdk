package org.aerogear.mobile.auth.authenticator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import junit.framework.Assert;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.aerogear.mobile.auth.AuthStateManager;
import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.authenticator.oidc.OIDCAuthenticatorImpl;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.AuthenticationException;
import org.aerogear.mobile.auth.credentials.JwksManager;
import org.aerogear.mobile.auth.credentials.OIDCCredentials;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.jose4j.jwk.JsonWebKeySet;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
public class OIDCAuthenticatorImplTest {

    private static final String EXTRA_RESPONSE = "net.openid.appauth.AuthorizationResponse";

    @Mock
    private ServiceConfiguration serviceConfig;

    private String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJlMzkzOGU2Zi0zOGQzLTQ2MmYtYTg1OS04YjNiODA0N2NlNzkiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE2NjMyNjQ3LCJzZXNzaW9uX3N0YXRlIjoiYzI1NWYwYWMtODA5MS00YzkyLThmM2EtNDhmZmI4ODFhNzBiIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.RvsLrOrLB3EFkZvYZM8-QXf6rRllCap-embNwa2V-NTMpcR7EKNMkKUQI9MbBlVSkTEBckZAK0DGSdo5CYuFoFH-xVWkzU0yQKBuFYAK1Etd50yQWwS1vHiThT95ZgeGGCB3ptafY5UCoqyg41kKqO5rb8iGyZ3ACp2xoEOE5S1rPAPszcQrbKBryOOk7H6MDZgqhZxxGUJkDVAT2v3jAd1aJ4K17qH6raabtDrAy_541vn6c0LS1ay0ooW4IVFzjFSH1-jMJvCYM6oku7brPonl2qHO8jMLrrhxylw2VXIAlregih6aNJ5c87729UtEJNTEFyqGI6GCunt2DQt7cw";

    private static final String OIDC_RESPONSE = "{\"request\":{\"configuration\":{\"authorizationEndpoint\":\"https:\\/\\/keycloak.security." +
        "feedhenry.org\\/auth\\/realms\\/secure-app\\/protocol\\/openid-connect\\/auth\",\"tokenEndpoint\":\"https:\\/\\/keycloak.security." +
        "feedhenry.org\\/auth\\/realms\\/secure-app\\/protocol\\/openid-connect\\/token\"},\"clientId\":\"client-app\",\"responseType\":\"code\"," +
        "\"redirectUri\":\"org.aerogear.mobile.example:\\/callback\",\"scope\":\"openid\",\"state\":\"F5k-IpBk78msD4WDhTsH5A\",\"codeVerifier\":" +
        "\"OhbL1LpIfgmi-43ZXunglZO7W1fw7sCXEASJ5WI1R1FCnWATI6nTojkD0I1i0nMRnLY12zOY1P9Diwh3hMcYLQ\",\"codeVerifierChallenge\":" +
        "\"id9PEuSokk2pbZ7nodtnrtclgM0JefxkfeIX1lqxNUY\",\"codeVerifierChallengeMethod\":\"S256\",\"additionalParameters\":{}},\"state\":" +
        "\"F5k-IpBk78msD4WDhTsH5A\",\"code\":\"eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0..RfNJA4YwXlSa1NbvZWAaxA.DcT007_SRl-" +
        "ndQeYBIg2UImGpYW1fO6HDOpSdrn2oOE3EGiyDeQtkWotx_F69-7vvgZJkUEgGZH4ITo9OAwK4hDtKRAOsTIvfOEb2cFxAk36iieAAtMipvdMTMaMFufH306xd-" +
        "pvAki0_Qz1B44rMyKGeni3kKYJKpag5JFrbxtu7nqZXlS5pAksXH92aBePYEM2LjpgH3a3ZC-CbR8AB-JJZBb_WdhuDd7TRjMI16d73EAT-qsSM_u4fpaNb6_x." +
        "Gqrn4dPmbfRtxWv1s4vNSg\",\"additional_parameters\":{}}";


    private static final String AUTH_STATE = "{\"refreshToken\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUl" +
        "pmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiI1YWFjZDI3Mi1jYmU3LTQ0MTYtOWY3ZS04NmY3NTc4NWNjYjQiLCJleHAiOjE1MTkwNTA5ODQsIm5iZiI6MCwiaWF0I" +
        "joxNTE5MDQ5MTg0LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAi" +
        "LCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiY2xpZW50LWFwcCIsImF1dGhfdGltZSI6MCwic2Vzc2l" +
        "vbl9zdGF0ZSI6ImUxMmUzMTYwLTAxYjctNDgxMy1iMTg0LTJhMjQ2MzY4YTY0MSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYW" +
        "NjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX19.c2MhJ30ruGOYjTDGO0FN" +
        "OZIgJsgeT2PxE001Nv1J7qRA7HVxMtRl3FcbqDSfdIv_LpNQHczcV5_ZE5jCt0JJQaNuPHR9x9aWKaXggTj1B01qJvzo1n36NiQi8GZvLrB2ZhUk3ydVPqPxEcXmnrToaDsA64_" +
        "6JoQDdceIJOn9PxwGmHd6aOKkwKYpecB2T5Af7slAOUW987d0dIrutVe3rxiVfXvoolgU0QAKoI9zhmBIMXKFFJAF_cFrpwEPdq-acqf7mSVlcqBJbvG1ljBhwgx9n2rDENqSJX" +
        "Hyn4wgy71GrLUWQPJUEIxU3IqYjQ_gKtlqgklGvmADo1ZAVxSv4w\",\"scope\":\"openid\",\"lastAuthorizationResponse\":{\"request\":{\"configuration\"" +
        ":{\"authorizationEndpoint\":\"https:\\/\\/keycloak.security.feedhenry.org\\/auth\\/realms\\/secure-app\\/protocol\\/openid-connect\\/auth" +
        "\",\"tokenEndpoint\":\"https:\\/\\/keycloak.security.feedhenry.org\\/auth\\/realms\\/secure-app\\/protocol\\/openid-connect\\/token\"},\"" +
        "clientId\":\"client-app\",\"responseType\":\"code\",\"redirectUri\":\"org.aerogear.mobile.example:\\/callback\",\"scope\":\"openid\",\"st" +
        "ate\":\"fVRggfBVhokGVpN0WAo-9Q\",\"codeVerifier\":\"13519A1KrnDsUPaqXEWMjD16jnaxu2UENncg6rS7CyLpoW0_4Mtsf6J5xQ-2N9eWiNV4fmMx_r9wE7Yno-cog" +
        "w\",\"codeVerifierChallenge\":\"UK1KXFmHZpaghF-abZng3v3OWA06Szn4VnKacFvXs2c\",\"codeVerifierChallengeMethod\":\"S256\",\"additionalParame" +
        "ters\":{}},\"state\":\"fVRggfBVhokGVpN0WAo-9Q\",\"code\":\"eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0..RTDc9LWmYC-t644qixGLVw.8eniDN" +
        "ul_WtuWiD2m3gpxk7H6QujVJ97cMB6Ltjsxuwx2QON5NlGHPZEjNbt-asiF-0No1lax5jO01-wHtWQlWeAmYiSqCSySNUCpP-qwMcym9bVx60Htn4IgJDxlONJ5FUrpIUKptPyhiV" +
        "UWY8C3ICJ2L3K34C_2Qcz3NX8iPrrER7lvY0z3AQCa2BgIoDw_zW6TCD3ihvZIy9X2663cYa2ODGYLE06Ev7JZhi7xEST7cfa5loAkGPcNJcdx5Hj.3ERuukmiTpU4kdkuJ7pBWg\"" +
        ",\"additional_parameters\":{}},\"mLastTokenResponse\":{\"request\":{\"configuration\":{\"authorizationEndpoint\":\"https:\\/\\/keycloak.s" +
        "ecurity.feedhenry.org\\/auth\\/realms\\/secure-app\\/protocol\\/openid-connect\\/auth\",\"tokenEndpoint\":\"https:\\/\\/keycloak.security" +
        ".feedhenry.org\\/auth\\/realms\\/secure-app\\/protocol\\/openid-connect\\/token\"},\"clientId\":\"client-app\",\"grantType\":\"authorizat" +
        "ion_code\",\"redirectUri\":\"org.aerogear.mobile.example:\\/callback\",\"scope\":\"openid\",\"authorizationCode\":\"eyJhbGciOiJkaXIiLCJlb" +
        "mMiOiJBMTI4Q0JDLUhTMjU2In0..RTDc9LWmYC-t644qixGLVw.8eniDNul_WtuWiD2m3gpxk7H6QujVJ97cMB6Ltjsxuwx2QON5NlGHPZEjNbt-asiF-0No1lax5jO01-wHtWQlW" +
        "eAmYiSqCSySNUCpP-qwMcym9bVx60Htn4IgJDxlONJ5FUrpIUKptPyhiVUWY8C3ICJ2L3K34C_2Qcz3NX8iPrrER7lvY0z3AQCa2BgIoDw_zW6TCD3ihvZIy9X2663cYa2ODGYLE0" +
        "6Ev7JZhi7xEST7cfa5loAkGPcNJcdx5Hj.3ERuukmiTpU4kdkuJ7pBWg\",\"additionalParameters\":{}},\"token_type\":\"bearer\",\"access_token\":\"eyJh" +
        "bGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJlMTgzZmZkYS1hNTIwL" +
        "TRkZWMtOTEzOC1mMDk0NGY5Nzc5YzYiLCJleHAiOjE1MTkwNTA5ODQsIm5iZiI6MCwiaWF0IjoxNTE5MDQ5MTg0LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZW" +
        "RoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXA" +
        "iOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE5MDQ5MTg0LCJzZXNzaW9uX3N0YXRlIjoiZTEyZTMxNjAtMDFiNy00ODEzLWIxODQtMmEyNDYzNjhh" +
        "NjQxIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJjb20uZmVlZGhlbnJ5LnNlY3VyZW5hdGl2ZWFuZHJvaWR0ZW1wbGF0ZSIsImNvbS5mZWVkaGVucnkuc3NvIiwiaHR0c" +
        "DovL2xvY2FsaG9zdDo4MTAwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibW" +
        "FuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sIm5hbWUiOiJVc2VyIDEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyMSIsImd" +
        "pdmVuX25hbWUiOiJVc2VyIiwiZmFtaWx5X25hbWUiOiIxIiwiZW1haWwiOiJ1c2VyMUBmZWVkaGVucnkub3JnIn0.Gom6PqeECCY1wJX_1RNWIkqajqFuk5Ehc3QRAFWjTClm4vhf" +
        "Al71f9K1hOV4f7dN17igriccGKAGL84a7kRGiWTczasFD9QwdYKqICESVpUEE_SJGzepM5qrCzvtuZ_og4TmJkqMSYwPiHkkTD_roiIkhAWA9bqdC2ec2XNo_icdRXXcCvbgk-Vcw" +
        "iI6mKfCRHuTyC1Ri_pCAorPXWTBQVL6eR75rawg5OljZVDhM4Vu9fdul42bD1OLffGQRN4wsR1bWl5zm7Fh9Wucv0sj4hEh-z_3-D9KdkLqsM-yggwLgKmeO-j_I8ENjzQkbxGkLD" +
        "zoLKq_ADfvyRu9JRiG2Q\",\"expires_at\":1519050983987,\"id_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2" +
        "VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiI1ZmQ4OTE1YS1kMjMwLTQ3NGQtODcyYy05NDIwMzNhNGIwNzkiLCJleHAiOjE1MTkwNTA5ODQsIm5iZiI6MCw" +
        "iaWF0IjoxNTE5MDQ5MTg0LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1h" +
        "cHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJJRCIsImF6cCI6ImNsaWVudC1hcHAiLCJhdXRoX3RpbWUiOjE1MTkwNDkxODQsI" +
        "nNlc3Npb25fc3RhdGUiOiJlMTJlMzE2MC0wMWI3LTQ4MTMtYjE4NC0yYTI0NjM2OGE2NDEiLCJhY3IiOiIxIiwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6In" +
        "VzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.i03AarSr8UuqzOh0ziUDHdnh3B5Wpm-Az01R" +
        "Su_vM_dJHGZ8OC7L1sh7Nz_GmtjyDumPMtpRZkWNnX6k8FJs_5enThF0Tsoba-Lv4yikxtkMpVYx70QutzUQB91rIKi0MXohSeDzds9Uj5_iNtsR7BBtSMH-C6TbytuDaSpw3KlM-" +
        "quH3Ek1tTr4ZVXQJy7ZnBSpCGGjF-XP0oYASLbIPMnfzqL1NQLEvUKShdz8VLS8Bpz4fZ7dzPL1U1nSV2PAYd9Va5lm6QLh3OqCXt4rxYFz1Xa6hfgYadyp4YtkLIWIJHZXWON5vQ" +
        "DvMnurFbzIlna7Igt1owhSWgdyzsSmRw\",\"refresh_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNm" +
        "hOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiI1YWFjZDI3Mi1jYmU3LTQ0MTYtOWY3ZS04NmY3NTc4NWNjYjQiLCJleHAiOjE1MTkwNTA5ODQsIm5iZiI6MCwiaWF0IjoxNTE" +
        "5MDQ5MTg0LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIi" +
        "OiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiY2xpZW50LWFwcCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0Z" +
        "SI6ImUxMmUzMTYwLTAxYjctNDgxMy1iMTg0LTJhMjQ2MzY4YTY0MSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7Im" +
        "FjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX19.c2MhJ30ruGOYjTDGO0FNOZIgJsgeT2PxE0" +
        "01Nv1J7qRA7HVxMtRl3FcbqDSfdIv_LpNQHczcV5_ZE5jCt0JJQaNuPHR9x9aWKaXggTj1B01qJvzo1n36NiQi8GZvLrB2ZhUk3ydVPqPxEcXmnrToaDsA64_6JoQDdceIJOn9Pxw" +
        "GmHd6aOKkwKYpecB2T5Af7slAOUW987d0dIrutVe3rxiVfXvoolgU0QAKoI9zhmBIMXKFFJAF_cFrpwEPdq-acqf7mSVlcqBJbvG1ljBhwgx9n2rDENqSJXHyn4wgy71GrLUWQPJU" +
        "EIxU3IqYjQ_gKtlqgklGvmADo1ZAVxSv4w\",\"additionalParameters\":{\"refresh_expires_in\":\"1800\",\"not-before-policy\":\"1518687910\",\"ses" +
        "sion_state\":\"e12e3160-01b7-4813-b184-2a246368a641\"}}}";

    private OIDCAuthenticatorImpl authenticator;

    private OIDCCredentials credential;

    private AuthServiceConfiguration authServiceConfiguration;

    @Mock
    private Activity activity;

    @Mock
    private Context context;

    @Mock
    private AuthStateManager authStateManager;

    @Mock
    private AuthorizationServiceFactory authorizationServiceFactory;

    @Mock
    AuthorizationServiceFactory.ServiceWrapper serviceWrapper;

    @Mock
    AuthorizationService authorizationService;

    @Mock
    private Intent intent;

    @Mock
    private AuthState authState;

    @Mock
    private TokenResponse tokenResponse;

    @Mock
    private JwksManager jwksManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(serviceConfig.getProperty(anyString())).thenReturn("dummyvalue");

        when(serviceWrapper.getAuthorizationService()).thenReturn(authorizationService);
        when(serviceWrapper.getAuthState()).thenReturn(authState);

        when(authorizationServiceFactory.createAuthorizationService(any(), any())).thenReturn(serviceWrapper);

        when(intent.getStringExtra(EXTRA_RESPONSE)).thenReturn(OIDC_RESPONSE);
        when(intent.hasExtra(EXTRA_RESPONSE)).thenReturn(true);

        when(authState.jsonSerializeString()).thenReturn(AUTH_STATE);

        doAnswer(invocation -> {
            ((AuthorizationService.TokenResponseCallback)invocation.getArguments()[1]).onTokenRequestCompleted(tokenResponse, null);
            return null;
        }).when(authorizationService).performTokenRequest(
                any(TokenRequest.class),
                any(AuthorizationService.TokenResponseCallback.class));

        credential = new OIDCCredentials() {
            @Override
            public String getAccessToken(){
                return accessToken;
            }
        };
        authServiceConfiguration = new AuthServiceConfiguration.AuthConfigurationBuilder().withRedirectUri("some.redirect.uri:/callback").build();
        authenticator = new OIDCAuthenticatorImpl(serviceConfig, authServiceConfiguration, authStateManager, authorizationServiceFactory, jwksManager);

        doAnswer(invocation -> {
            ((Callback<JsonWebKeySet>)invocation.getArguments()[1]).onSuccess(null);
            return null;
        }).when(jwksManager).fetchJwks(any(), any(Callback.class));
    }

    @Test
    public void testAuthenticate() throws AuthenticationException, IOException, JSONException  {
        DefaultAuthenticateOptions opts = new DefaultAuthenticateOptions(activity, 0);

        authenticator.authenticate(opts, new Callback<UserPrincipal>() {
            @Override
            public void onSuccess(UserPrincipal userPrincipal) {
                Assert.assertEquals("user1", userPrincipal.getName());
                Assert.assertEquals("user1@feedhenry.org", userPrincipal.getEmail());
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail("Error received " + error);
            }
        });

        authenticator.handleAuthResult(intent);
    }

}
