package org.aerogear.mobile.auth.credentials;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.jose4j.jwk.JsonWebKeySet;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import org.aerogear.mobile.auth.AuthenticationException;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;

import junit.framework.Assert;
import net.openid.appauth.AuthState;

@RunWith(RobolectricTestRunner.class)
public class OIDCCredentialsTest {

    private final String VALID_PUBLIC_KEY =
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkr1fDOUrTZc1MnpY9brGiA7Cz6X1nX77pmrUEgnMq2mxU7ibSW0CAk5e5a4wkmLGYf8EyvaFPHT1fMrFmDK03oN8Q2anh+3e894cXBXazHzzaJD+Lz1HfOOZFeInkAasxWSo8KN1+Kg+1Z7QyrPLhfcbIwfH2Stabx+3lfEMtPGws7tqWg93piA8is1PwIV5/8k4CqLe7jNtUyYS4BKR07oBY6VVxXOKKQAQ3ToLN++sjfaXAjDuE1Go7iW9q7Yt6q9qu4JCX+k6IWu68y/H6cicLXwS1VXPMwFjDOj7cQZB7A3t4q0F+6NVL+t7UjrAAK/7V3lPB+rDwHO92iwlZwIDAQAB";
    private final String VALID_KEYCLOAK_HOST_URL = "https://keycloak.security.feedhenry.org/auth";
    private final String VALID_AUDIENCE = "client-app";
    private final String EXPIRED_ACCESS_TOKEN =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJkMWVmMWIyMS00OTVlLTRjYjMtYmQzNS1mMTM5YzcxNGFlOWIiLCJleHAiOjE1MTY2MjA5MzgsIm5iZiI6MCwiaWF0IjoxNTE2NjIwNjM4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE2NjIwNjM3LCJzZXNzaW9uX3N0YXRlIjoiODg0MzZjMTAtNjQ3Yy00OTM4LTgwZmYtODAzMTBhYjc1NTNhIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.Hy7mII0Z4d70jcZJTvDeIRi_tZPp7k5qID-c94XmpTUrCT7YJkWjV158oq5iG2bs-RdHSdmUYSCc2ZrUMWAUmsdVxUe621oLzI50csqW3iXQfEO4urUYYFHIknkqP76PIdwFW80zMANCeiXMZEy8D4iJ_UkzDoo872w4iCNApZVnFxk2S15WbyPQPiXbSaMjLkZsEiKKjzfacDtPtSpgbtq9s5DuU7QrBvkmGZYofY94-gORglXpu3KIhs4oXmk2-2CTDqsOpV-eS1OyeZQ080Xfr5N9X7Rfe-fvTToTuFiuMOPOpzgkYDUuCU7Tcymp7EXjpBf2YX65m3QtYY9SYA";
    // WARNING: Tokens Expire on 10/01/2031 @ 2:50pm (UTC)
    private final String VALID_REFRESH_TOKEN =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIwMGJhODRlYy01MTYzLTRhZDgtYWNmMC04ZTQ2Mzc4OWE0MmUiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiY2xpZW50LWFwcCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6ImMyNTVmMGFjLTgwOTEtNGM5Mi04ZjNhLTQ4ZmZiODgxYTcwYiIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fX0.Df_B_o7xaVyF6yGzyjfZJLwF6J9jrmjlzkKGtWyBzblBFVs8HMNWhUen3QJIq_lJLEloXKMh0Hfd5Yo0iPbLlNFXb_qIuIUUDtHuQHEL5-LZbTW8WCvotbfVbO9hTHxXJXs5sWdL3wcS1T5PuC5l6mP1a1fppvkWPOlcRFWVy9f-aBSRiUNGoN_AlL2FUIeFeHKeyfw7F5EndQt9HRfoFr0vD1VuB7uM2UWhnscCwsFWZ2AMz_ZoiUQPjtUm-y3UMURFcAhIYHCw7xI1tPRVdMkMC8i87Qq9HJ90eflEDjqse6x67m48kT1wvI2qhoexPa-ALdWMrseWIdRqBmSUHw";
    private final String VALID_IDENTITY_TOKEN =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIzMmJjMjc4Yy00MmUyLTQwMjctOWVkZi1mZTM3OTcxYjkyZTQiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJJRCIsImF6cCI6ImNsaWVudC1hcHAiLCJhdXRoX3RpbWUiOjE1MTY2MzI2NDcsInNlc3Npb25fc3RhdGUiOiJjMjU1ZjBhYy04MDkxLTRjOTItOGYzYS00OGZmYjg4MWE3MGIiLCJhY3IiOiIxIiwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.h_WzsxdBUG0eBMz_zgEyl0yOMlVJ1YkNTRaiWje7Do3s90URP3y_nFkz9NNMSfoADPLZdTWe9fWL1h2LWfc6WUmasr4hx9zks76Asz78rZLyC4ARuGSwUncCiLxdB4chq-SAOoXYiZV6K0St-UhBkV90jd6xc5WIKrTItqDenuMjkc5ePU7Sx9MYE2fS4dKGNdRGff8uAXELpM66eVxM6xn-xBTEuHPQvR-vFoFLUssxMVk8IjsAb2H9epioWshQkY0vL70z58egAqiLVluVFecRXmsrLGrVnRx-S3UjQRKLyo_V3CyFCfGKspGtsXZYhmzzmMTdqPU_wLax59A69w";
    private final String VALID_ACCESS_TOKEN =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJlMzkzOGU2Zi0zOGQzLTQ2MmYtYTg1OS04YjNiODA0N2NlNzkiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE2NjMyNjQ3LCJzZXNzaW9uX3N0YXRlIjoiYzI1NWYwYWMtODA5MS00YzkyLThmM2EtNDhmZmI4ODFhNzBiIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.RvsLrOrLB3EFkZvYZM8-QXf6rRllCap-embNwa2V-NTMpcR7EKNMkKUQI9MbBlVSkTEBckZAK0DGSdo5CYuFoFH-xVWkzU0yQKBuFYAK1Etd50yQWwS1vHiThT95ZgeGGCB3ptafY5UCoqyg41kKqO5rb8iGyZ3ACp2xoEOE5S1rPAPszcQrbKBryOOk7H6MDZgqhZxxGUJkDVAT2v3jAd1aJ4K17qH6raabtDrAy_541vn6c0LS1ay0ooW4IVFzjFSH1-jMJvCYM6oku7brPonl2qHO8jMLrrhxylw2VXIAlregih6aNJ5c87729UtEJNTEFyqGI6GCunt2DQt7cw";

    private final String TAMPERED_REFRESH_TOKEN =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIwMGJhODRlYy01MTYzLTRhZDgtYWNmMC04ZTQ2Mzc4OWE0MmUiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3cyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiY2xpZW50LWFwcCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6ImMyNTVmMGFjLTgwOTEtNGM5Mi04ZjNhLTQ4ZmZiODgxYTcwYiIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fX0.Df_B_o7xaVyF6yGzyjfZJLwF6J9jrmjlzkKGtWyBzblBFVs8HMNWhUen3QJIq_lJLEloXKMh0Hfd5Yo0iPbLlNFXb_qIuIUUDtHuQHEL5-LZbTW8WCvotbfVbO9hTHxXJXs5sWdL3wcS1T5PuC5l6mP1a1fppvkWPOlcRFWVy9f-aBSRiUNGoN_AlL2FUIeFeHKeyfw7F5EndQt9HRfoFr0vD1VuB7uM2UWhnscCwsFWZ2AMz_ZoiUQPjtUm-y3UMURFcAhIYHCw7xI1tPRVdMkMC8i87Qq9HJ90eflEDjqse6x67m48kT1wvI2qhoexPa-ALdWMrseWIdRqBmSUHw\n";
    private final String TAMPERED_IDENTITY_TOKEN =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIzMmJjMjc4Yy00MmUyLTQwMjctOWVkZi1mZTM3OTcxYjkyZTQiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJJRCIsImF6cCI6ImNsaWVudC1hcHAiLCJhdXRoX3RpbWUiOjE1MTY2MzI2NDcsInNlc3Npb25fc3RhdGUiOiJjMjU1ZjBhYy04MDkxLTRjOTItOGYzYS00OGZmYjg4MWE3MGIiLCJhY3IiOiIxIiwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZsIxQGZlZWRoZW5yeS5vcmcifQ.h_WzsxdBUG0eBMz_zgEyl0yOMlVJ1YkNTRaiWje7Do3s90URP3y_nFkz9NNMSfoADPLZdTWe9fWL1h2LWfc6WUmasr4hx9zks76Asz78rZLyC4ARuGSwUncCiLxdB4chq-SAOoXYiZV6K0St-UhBkV90jd6xc5WIKrTItqDenuMjkc5ePU7Sx9MYE2fS4dKGNdRGff8uAXELpM66eVxM6xn-xBTEuHPQvR-vFoFLUssxMVk8IjsAb2H9epioWshQkY0vL70z58egAqiLVluVFecRXmsrLGrVnRx-S3UjQRKLyo_V3CyFCfGKspGtsXZYhmzzmMTdqPU_wLax59A69w\n";
    private final String TAMPERED_ACCESS_TOKEN =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJlMzkzOGU2Zi0zOGQzLTQ2MmYtYTg1OS04YjNiODA0N2NlNzkiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE2NjMyNjQ3LCJzZXNzaW9uX3N0YXRlIjoiYzI1NWYwYWMtODA5MS00YzkyLThmM2EtNDhmZmI4ODFhNzBiIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVtdC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNub3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.RvsLrOrLB3EFkZvYZM8-QXf6rRllCap-embNwa2V-NTMpcR7EKNMkKUQI9MbBlVSkTEBckZAK0DGSdo5CYuFoFH-xVWkzU0yQKBuFYAK1Etd50yQWwS1vHiThT95ZgeGGCB3ptafY5UCoqyg41kKqO5rb8iGyZ3ACp2xoEOE5S1rPAPszcQrbKBryOOk7H6MDZgqhZxxGUJkDVAT2v3jAd1aJ4K17qH6raabtDrAy_541vn6c0LS1ay0ooW4IVFzjFSH1-jMJvCYM6oku7brPonl2qHO8jMLrrhxylw2VXIAlregih6aNJ5c87729UtEJNTEFyqGI6GCunt2DQt7cw\n";

    private static final String CREDENTIAL_AUTH_STATE = new AuthState().jsonSerializeString();

    @Mock
    private AuthState authState;

    @Mock
    private KeycloakConfiguration keycloakConfiguration;

    private static final String ACCESS_TOKEN =
                    "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiI5YjU3NzdiOS04ZmVlLTQ2N2ItYmZiMS01YWQ0N2QyZTkxYjMiLCJleHAiOjE1MTkxNDcwNDMsIm5iZiI6MCwiaWF0IjoxNTE5MTQ1MjQzLCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE5MTQ1MjQzLCJzZXNzaW9uX3N0YXRlIjoiYjgyZjRjMzAtMDNlYy00MWI5LTkyZDgtZTQ2OTRlMDc4NTYwIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJjb20uZmVlZGhlbnJ5LnNlY3VyZW5hdGl2ZWFuZHJvaWR0ZW1wbGF0ZSIsImNvbS5mZWVkaGVucnkuc3NvIiwiaHR0cDovL2xvY2FsaG9zdDo4MTAwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sIm5hbWUiOiJVc2VyIDEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyMSIsImdpdmVuX25hbWUiOiJVc2VyIiwiZmFtaWx5X25hbWUiOiIxIiwiZW1haWwiOiJ1c2VyMUBmZWVkaGVucnkub3JnIn0.KTFmWR8o8y62PyNd02rqIbpOG9YkaFEMu-YdryDxi_LQ5UFLt39oAy0KjnPwERI9j3RwQD3MOhO6HwQMw0-K_9NBzjtx4O7N-MXEHMZJor8GsNLeB44G_3HA8fWO7LwAG21b0PlkotKamtT23QIwYtLHwestiX48bMyqcR1NNDdAO-PnTSi7aYE9ecl50nJGFxy22cKq3gJsf8G3Cs3WYtKsE0YjOborZH2ibHJpRsnFgX1KRt7pUEvMcMiRst7J_cgylXThISXmTKtc2gPY2l5HNUi8a3AbSHkIP48VqCj95ysrIAnOvZtYdFpqStiHsne5jTVYn4u3an46FOG0qw";

    private static final String JWKS_CONTENT =
                    "{\"keys\":[{\"kid\":\"adSoyXNAgQxV43eqHSiRZf6hN9ytvBNQyb2fFSdCTVM\",\"kty\":\"RSA\","
                                    + "\"alg\":\"RS256\",\"use\":\"sig\",\"n\":\"kr1fDOUrTZc1MnpY9brGiA7Cz6X1nX77pmrUEgnMq2mxU7ibSW0CAk5e5a4wkmLGYf8Ey"
                                    + "vaFPHT1fMrFmDK03oN8Q2anh-3e894cXBXazHzzaJD-Lz1HfOOZFeInkAasxWSo8KN1-Kg-1Z7QyrPLhfcbIwfH2Stabx-3lfEMtPGws7tqWg93"
                                    + "piA8is1PwIV5_8k4CqLe7jNtUyYS4BKR07oBY6VVxXOKKQAQ3ToLN--sjfaXAjDuE1Go7iW9q7Yt6q9qu4JCX-k6IWu68y_H6cicLXwS1VXPMwF"
                                    + "jDOj7cQZB7A3t4q0F-6NVL-t7UjrAAK_7V3lPB-rDwHO92iwlZw\",\"e\":\"AQAB\"}]}";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(keycloakConfiguration.getHostUrl()).thenReturn(VALID_KEYCLOAK_HOST_URL);
        when(keycloakConfiguration.getResourceId()).thenReturn(VALID_AUDIENCE);
    }

    @Test
    public void testSerialize() throws JSONException {
        OIDCCredentials testCredential = new OIDCCredentials(CREDENTIAL_AUTH_STATE);
        JSONObject serializedCredential = new JSONObject(testCredential.serialize());
        assertEquals(serializedCredential.get("authState"), CREDENTIAL_AUTH_STATE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRenew() throws AuthenticationException {
        OIDCCredentials testCredential = new OIDCCredentials(CREDENTIAL_AUTH_STATE);
        testCredential.renew();
    }

    @Test
    public void testDeserialize() {
        OIDCCredentials testCredential = new OIDCCredentials(CREDENTIAL_AUTH_STATE);
        String serialized = testCredential.serialize();

        OIDCCredentials deserialised = OIDCCredentials.deserialize(serialized);
        Assert.assertTrue(testCredential.equals(deserialised));
    }

    @Test
    public void testVerifyClaimsOk() throws Exception {

        OIDCCredentials testCredential = new OIDCCredentials(CREDENTIAL_AUTH_STATE) {
            @Override
            public String getAccessToken() {
                return VALID_ACCESS_TOKEN;
            }
        };

        JsonWebKeySet keySet = new JsonWebKeySet(JWKS_CONTENT);
        boolean verified = testCredential.verifyClaims(keySet, keycloakConfiguration);
        Assert.assertTrue(verified);
    }

    @Test
    public void testVerifyClaimsExpired() throws Exception {

        OIDCCredentials testCredential = new OIDCCredentials(CREDENTIAL_AUTH_STATE) {
            @Override
            public String getAccessToken() {
                return EXPIRED_ACCESS_TOKEN;
            }
        };

        JsonWebKeySet keySet = new JsonWebKeySet(JWKS_CONTENT);
        boolean verified = testCredential.verifyClaims(keySet, keycloakConfiguration);
        Assert.assertFalse(verified);
    }
}
