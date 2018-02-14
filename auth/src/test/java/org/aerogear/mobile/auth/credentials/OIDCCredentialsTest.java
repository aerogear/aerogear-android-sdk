package org.aerogear.mobile.auth.credentials;

import net.openid.appauth.AuthState;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class OIDCCredentialsTest {

    private final String VALID_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkr1fDOUrTZc1MnpY9brGiA7Cz6X1nX77pmrUEgnMq2mxU7ibSW0CAk5e5a4wkmLGYf8EyvaFPHT1fMrFmDK03oN8Q2anh+3e894cXBXazHzzaJD+Lz1HfOOZFeInkAasxWSo8KN1+Kg+1Z7QyrPLhfcbIwfH2Stabx+3lfEMtPGws7tqWg93piA8is1PwIV5/8k4CqLe7jNtUyYS4BKR07oBY6VVxXOKKQAQ3ToLN++sjfaXAjDuE1Go7iW9q7Yt6q9qu4JCX+k6IWu68y/H6cicLXwS1VXPMwFjDOj7cQZB7A3t4q0F+6NVL+t7UjrAAK/7V3lPB+rDwHO92iwlZwIDAQAB";
    private final String INVALID_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh86f89U+DJkuCaxFUATgiQS4TWx/kyY1xSDOWKPMVddL2mT4H44tcMf2A/aYJnFJL24zg42f3nt8hHnX4o2O0zlh1T7E60riCtKVn2BT4hBQXMMhUhVmNL8RHP5s4X3WFBN9q/yLisXLHYJ9OCnsah4ZFQOAbwiqVNpY7+tVt2uw6pUrMhy65Dj6jspn96t6p/HCI1ZdCQwzRdlCx77ZOQywNTqKU9W4DibG3WU7DPmgHbrpCJjHCvK/+cMRtTx6lhH8W/cHVN8D5FZUiC61eEFPGwV5wa7KqrKsJE23YUCxemb36xV21zBXay/j1vSxhIGB6d6G+ckI0FkanfrMmQIDAQAB";
    private final String INVALID_PUBLIC_KEY_FORMAT = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkr1fDOUrTZc1MnpY9brGiA7Cz6X1nX77pmrUEgnMq2mxU7ibSW0CAk5e5a4wkmLGYf8EyvaFPHT1fMrFmDK03oN8Q2anh+3e894cXBXazHzzaJD+Lz1HfOOZFeInkAasxWSo8KN1+Kg+1Z7QyrPLhfcbIwfH2Stabx+3lfEMtPGws7tqWg93piA8is1PwIV5/8k4CqLe7jNtUyYS4BKR07oBY6VVxXOKKQAQ3ToLN++sjfaXAjDuE1Go7iW9q7Yt6q9qu4JCX+k6IWu68y/H6cicLXwS1VXPMwFjDOj7cQZB7A3t4q0F+6NVL+t7UjrAAK/7V3lPB+rDwHO92iwlZwIDAQAB-----END PUBLIC KEY-----";

    private final String VALID_ISSUER = "https://keycloak.security.feedhenry.org/auth/realms/secure-app";
    private final String VALID_AUDIENCE = "client-app";

    private final String INVALID_ISSUER = "https://invalid.com/issuer";
    private final String INVALID_AUDIENCE = "invalid-audience";

    private final String EXPIRED_ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJkMWVmMWIyMS00OTVlLTRjYjMtYmQzNS1mMTM5YzcxNGFlOWIiLCJleHAiOjE1MTY2MjA5MzgsIm5iZiI6MCwiaWF0IjoxNTE2NjIwNjM4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE2NjIwNjM3LCJzZXNzaW9uX3N0YXRlIjoiODg0MzZjMTAtNjQ3Yy00OTM4LTgwZmYtODAzMTBhYjc1NTNhIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.Hy7mII0Z4d70jcZJTvDeIRi_tZPp7k5qID-c94XmpTUrCT7YJkWjV158oq5iG2bs-RdHSdmUYSCc2ZrUMWAUmsdVxUe621oLzI50csqW3iXQfEO4urUYYFHIknkqP76PIdwFW80zMANCeiXMZEy8D4iJ_UkzDoo872w4iCNApZVnFxk2S15WbyPQPiXbSaMjLkZsEiKKjzfacDtPtSpgbtq9s5DuU7QrBvkmGZYofY94-gORglXpu3KIhs4oXmk2-2CTDqsOpV-eS1OyeZQ080Xfr5N9X7Rfe-fvTToTuFiuMOPOpzgkYDUuCU7Tcymp7EXjpBf2YX65m3QtYY9SYA";

    // WARNING: Tokens Expire on 10/01/2031 @ 2:50pm (UTC)
    private final String VALID_REFRESH_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIwMGJhODRlYy01MTYzLTRhZDgtYWNmMC04ZTQ2Mzc4OWE0MmUiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiY2xpZW50LWFwcCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6ImMyNTVmMGFjLTgwOTEtNGM5Mi04ZjNhLTQ4ZmZiODgxYTcwYiIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fX0.Df_B_o7xaVyF6yGzyjfZJLwF6J9jrmjlzkKGtWyBzblBFVs8HMNWhUen3QJIq_lJLEloXKMh0Hfd5Yo0iPbLlNFXb_qIuIUUDtHuQHEL5-LZbTW8WCvotbfVbO9hTHxXJXs5sWdL3wcS1T5PuC5l6mP1a1fppvkWPOlcRFWVy9f-aBSRiUNGoN_AlL2FUIeFeHKeyfw7F5EndQt9HRfoFr0vD1VuB7uM2UWhnscCwsFWZ2AMz_ZoiUQPjtUm-y3UMURFcAhIYHCw7xI1tPRVdMkMC8i87Qq9HJ90eflEDjqse6x67m48kT1wvI2qhoexPa-ALdWMrseWIdRqBmSUHw";
    private final String VALID_IDENTITY_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIzMmJjMjc4Yy00MmUyLTQwMjctOWVkZi1mZTM3OTcxYjkyZTQiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJJRCIsImF6cCI6ImNsaWVudC1hcHAiLCJhdXRoX3RpbWUiOjE1MTY2MzI2NDcsInNlc3Npb25fc3RhdGUiOiJjMjU1ZjBhYy04MDkxLTRjOTItOGYzYS00OGZmYjg4MWE3MGIiLCJhY3IiOiIxIiwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.h_WzsxdBUG0eBMz_zgEyl0yOMlVJ1YkNTRaiWje7Do3s90URP3y_nFkz9NNMSfoADPLZdTWe9fWL1h2LWfc6WUmasr4hx9zks76Asz78rZLyC4ARuGSwUncCiLxdB4chq-SAOoXYiZV6K0St-UhBkV90jd6xc5WIKrTItqDenuMjkc5ePU7Sx9MYE2fS4dKGNdRGff8uAXELpM66eVxM6xn-xBTEuHPQvR-vFoFLUssxMVk8IjsAb2H9epioWshQkY0vL70z58egAqiLVluVFecRXmsrLGrVnRx-S3UjQRKLyo_V3CyFCfGKspGtsXZYhmzzmMTdqPU_wLax59A69w";
    private final String VALID_ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJlMzkzOGU2Zi0zOGQzLTQ2MmYtYTg1OS04YjNiODA0N2NlNzkiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE2NjMyNjQ3LCJzZXNzaW9uX3N0YXRlIjoiYzI1NWYwYWMtODA5MS00YzkyLThmM2EtNDhmZmI4ODFhNzBiIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.RvsLrOrLB3EFkZvYZM8-QXf6rRllCap-embNwa2V-NTMpcR7EKNMkKUQI9MbBlVSkTEBckZAK0DGSdo5CYuFoFH-xVWkzU0yQKBuFYAK1Etd50yQWwS1vHiThT95ZgeGGCB3ptafY5UCoqyg41kKqO5rb8iGyZ3ACp2xoEOE5S1rPAPszcQrbKBryOOk7H6MDZgqhZxxGUJkDVAT2v3jAd1aJ4K17qH6raabtDrAy_541vn6c0LS1ay0ooW4IVFzjFSH1-jMJvCYM6oku7brPonl2qHO8jMLrrhxylw2VXIAlregih6aNJ5c87729UtEJNTEFyqGI6GCunt2DQt7cw";

    private final String TAMPERED_REFRESH_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIwMGJhODRlYy01MTYzLTRhZDgtYWNmMC04ZTQ2Mzc4OWE0MmUiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3cyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiY2xpZW50LWFwcCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6ImMyNTVmMGFjLTgwOTEtNGM5Mi04ZjNhLTQ4ZmZiODgxYTcwYiIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fX0.Df_B_o7xaVyF6yGzyjfZJLwF6J9jrmjlzkKGtWyBzblBFVs8HMNWhUen3QJIq_lJLEloXKMh0Hfd5Yo0iPbLlNFXb_qIuIUUDtHuQHEL5-LZbTW8WCvotbfVbO9hTHxXJXs5sWdL3wcS1T5PuC5l6mP1a1fppvkWPOlcRFWVy9f-aBSRiUNGoN_AlL2FUIeFeHKeyfw7F5EndQt9HRfoFr0vD1VuB7uM2UWhnscCwsFWZ2AMz_ZoiUQPjtUm-y3UMURFcAhIYHCw7xI1tPRVdMkMC8i87Qq9HJ90eflEDjqse6x67m48kT1wvI2qhoexPa-ALdWMrseWIdRqBmSUHw\n";
    private final String TAMPERED_IDENTITY_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiIzMmJjMjc4Yy00MmUyLTQwMjctOWVkZi1mZTM3OTcxYjkyZTQiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJJRCIsImF6cCI6ImNsaWVudC1hcHAiLCJhdXRoX3RpbWUiOjE1MTY2MzI2NDcsInNlc3Npb25fc3RhdGUiOiJjMjU1ZjBhYy04MDkxLTRjOTItOGYzYS00OGZmYjg4MWE3MGIiLCJhY3IiOiIxIiwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZsIxQGZlZWRoZW5yeS5vcmcifQ.h_WzsxdBUG0eBMz_zgEyl0yOMlVJ1YkNTRaiWje7Do3s90URP3y_nFkz9NNMSfoADPLZdTWe9fWL1h2LWfc6WUmasr4hx9zks76Asz78rZLyC4ARuGSwUncCiLxdB4chq-SAOoXYiZV6K0St-UhBkV90jd6xc5WIKrTItqDenuMjkc5ePU7Sx9MYE2fS4dKGNdRGff8uAXELpM66eVxM6xn-xBTEuHPQvR-vFoFLUssxMVk8IjsAb2H9epioWshQkY0vL70z58egAqiLVluVFecRXmsrLGrVnRx-S3UjQRKLyo_V3CyFCfGKspGtsXZYhmzzmMTdqPU_wLax59A69w\n";
    private final String TAMPERED_ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZFNveVhOQWdReFY0M2VxSFNpUlpmNmhOOXl0dkJOUXliMmZGU2RDVFZNIn0.eyJqdGkiOiJlMzkzOGU2Zi0zOGQzLTQ2MmYtYTg1OS04YjNiODA0N2NlNzkiLCJleHAiOjE5NDg2MzI2NDgsIm5iZiI6MCwiaWF0IjoxNTE2NjMyNjQ4LCJpc3MiOiJodHRwczovL2tleWNsb2FrLnNlY3VyaXR5LmZlZWRoZW5yeS5vcmcvYXV0aC9yZWFsbXMvc2VjdXJlLWFwcCIsImF1ZCI6ImNsaWVudC1hcHAiLCJzdWIiOiJiMTYxN2UzOC0zODczLTRhNDctOGE2Yy01YjgyMmFkYTI3NWUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjbGllbnQtYXBwIiwiYXV0aF90aW1lIjoxNTE2NjMyNjQ3LCJzZXNzaW9uX3N0YXRlIjoiYzI1NWYwYWMtODA5MS00YzkyLThmM2EtNDhmZmI4ODFhNzBiIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtb2JpbGUtdXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVtdC1hcHAiOnsicm9sZXMiOlsiaW9zLWFjY2VzcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNub3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlVzZXIgMSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIxIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IjEiLCJlbWFpbCI6InVzZXIxQGZlZWRoZW5yeS5vcmcifQ.RvsLrOrLB3EFkZvYZM8-QXf6rRllCap-embNwa2V-NTMpcR7EKNMkKUQI9MbBlVSkTEBckZAK0DGSdo5CYuFoFH-xVWkzU0yQKBuFYAK1Etd50yQWwS1vHiThT95ZgeGGCB3ptafY5UCoqyg41kKqO5rb8iGyZ3ACp2xoEOE5S1rPAPszcQrbKBryOOk7H6MDZgqhZxxGUJkDVAT2v3jAd1aJ4K17qH6raabtDrAy_541vn6c0LS1ay0ooW4IVFzjFSH1-jMJvCYM6oku7brPonl2qHO8jMLrrhxylw2VXIAlregih6aNJ5c87729UtEJNTEFyqGI6GCunt2DQt7cw\n";

    private static final String CREDENTIAL_AUTH_STATE = new AuthState().jsonSerializeString();
    private static final String INTEGRITY_CHECK_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkr1fDOUrTZc1MnpY9brGiA7Cz6X1nX77pmrUEgnMq2mxU7ibSW0CAk5e5a4wkmLGYf8EyvaFPHT1fMrFmDK03oN8Q2anh+3e894cXBXazHzzaJD+Lz1HfOOZFeInkAasxWSo8KN1+Kg+1Z7QyrPLhfcbIwfH2Stabx+3lfEMtPGws7tqWg93piA8is1PwIV5/8k4CqLe7jNtUyYS4BKR07oBY6VVxXOKKQAQ3ToLN++sjfaXAjDuE1Go7iW9q7Yt6q9qu4JCX+k6IWu68y/H6cicLXwS1VXPMwFjDOj7cQZB7A3t4q0F+6NVL+t7UjrAAK/7V3lPB+rDwHO92iwlZwIDAQAB";
    private static final String INTEGRITY_CHECK_ISSUER = "testIssuer";
    private static final String INTEGRITY_CHECK_AUDIENCE = "testAudience";

    private String testSerializedCredential = "{ \"authState\": {}, " +
        " \"integrityCheck\": { \"issuer\": \"" + INTEGRITY_CHECK_ISSUER +
        "\", \"audience\": \"" + INTEGRITY_CHECK_AUDIENCE +
        "\", \"publicKey\": \"" + VALID_PUBLIC_KEY + "\" } } ";
    private OIDCCredentials testCredential;
    private OIDCCredentials testEmptyCredential;

    @Before
    public void setup() throws JSONException {
        IntegrityCheckParametersImpl checkParameters = new IntegrityCheckParametersImpl(INTEGRITY_CHECK_AUDIENCE, INTEGRITY_CHECK_ISSUER, INTEGRITY_CHECK_KEY);
        this.testCredential = new OIDCCredentials(CREDENTIAL_AUTH_STATE, checkParameters);
        this.testEmptyCredential = new OIDCCredentials();
    }

    @Test
    public void testValidPublicKey() {
        assertTrue("Expect Token Validation with Correct Public Key to Succeed", testEmptyCredential.verifyToken(VALID_ACCESS_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testInvalidPublicKey() {
        assertFalse("Expect Token Validation with Wrong Public Key to Fail", testEmptyCredential.verifyToken(VALID_ACCESS_TOKEN, INVALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testInvalidPublicKeyFormat() {
        assertFalse("Expect Token Validation with Wrong Public Key Format to Fail", testEmptyCredential.verifyToken(VALID_ACCESS_TOKEN, INVALID_PUBLIC_KEY_FORMAT, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testValidAudience() {
        assertTrue("Expect Token Validation with Valid Audience to Succeed", testEmptyCredential.verifyToken(VALID_ACCESS_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testInvalidAudience() {
        assertFalse("Expect Token Validation with Wrong Audience to Fail", testEmptyCredential.verifyToken(VALID_ACCESS_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, INVALID_AUDIENCE));
    }

    @Test
    public void testValidIssuer() {
        assertTrue("Expect Token Validation with Valid Issuer to Succeed", testEmptyCredential.verifyToken(VALID_ACCESS_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testInvalidIssuer() {
        assertFalse("Expect Token Validation with Wrong Issuer to Fail", testEmptyCredential.verifyToken(VALID_ACCESS_TOKEN, VALID_PUBLIC_KEY, INVALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testExpiredExpirationTime() {
        assertFalse("Expect Token Validation with Expired Time to Fail", testEmptyCredential.verifyToken(EXPIRED_ACCESS_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testValidAccessToken() {
        assertTrue("Expect Token Validation with Valid Access Token to Succeed", testEmptyCredential.verifyToken(VALID_ACCESS_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testTamperedAccessToken() {
        assertFalse("Expect Token Validation with Tampered Access Token to Fail", testEmptyCredential.verifyToken(TAMPERED_ACCESS_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testValidIdentityToken() {
        assertTrue("Expect Token Validation with Valid Identity Token to Succeed", testEmptyCredential.verifyToken(VALID_IDENTITY_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testTamperedIdentityToken() {
        assertFalse("Expect Token Validation with Tampered Identity Token to Fail", testEmptyCredential.verifyToken(TAMPERED_IDENTITY_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testValidRefreshToken() {
        assertTrue("Expect Token Validation with Valid Refresh Token to Succeed", testEmptyCredential.verifyToken(VALID_REFRESH_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testTamperedRefreshToken() {
        assertFalse("Expect Token Validation with Tampered Refresh Token to Fail", testEmptyCredential.verifyToken(TAMPERED_REFRESH_TOKEN, VALID_PUBLIC_KEY, VALID_ISSUER, VALID_AUDIENCE));
    }

    @Test
    public void testSerialize() throws JSONException {
        JSONObject serializedCredential = new JSONObject(this.testCredential.serialize());
        assertEquals(serializedCredential.get("authState"), CREDENTIAL_AUTH_STATE);
        assertNotNull(serializedCredential.getString("integrityCheck"));
    }

    @Test
    public void testDeserialize() throws JSONException {
        OIDCCredentials credential = OIDCCredentials.deserialize(testSerializedCredential);
        assertEquals(credential.getIntegrityCheckParameters().getAudience(), INTEGRITY_CHECK_AUDIENCE);
        assertEquals(credential.getIntegrityCheckParameters().getIssuer(), INTEGRITY_CHECK_ISSUER);
        assertEquals(credential.getIntegrityCheckParameters().getPublicKey(), VALID_PUBLIC_KEY);
    }

}
