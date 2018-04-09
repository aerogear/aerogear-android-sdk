package org.aerogear.mobile.auth;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.Context;
import android.content.SharedPreferences;

import org.aerogear.mobile.auth.credentials.OIDCCredentials;

public class AuthStateManagerTest {
    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private OIDCCredentials mockOIDCCredentials;
    @Mock
    private SharedPreferences.Editor mockSharedPreferencesEditor;

    private AuthStateManager authStateManager;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);

        when(mockContext.getSharedPreferences(anyString(), anyInt()))
                        .thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor);

        // Reset the singleton for each test
        Field authStateInstance = AuthStateManager.class.getDeclaredField("instance");
        authStateInstance.setAccessible(true);
        authStateInstance.set(null, null);
        authStateManager = AuthStateManager.getInstance(mockContext);
    }

    @Test
    public void testLoadWithEmptyStore() {
        OIDCCredentials authState = authStateManager.load();

        assertNull(authState.getAccessToken());
        assertNull(authState.getIdentityToken());
        assertNull(authState.getRefreshToken());
    }

    @Test
    public void testSaveNull() {
        when(mockSharedPreferencesEditor.remove(anyString()))
                        .thenReturn(mockSharedPreferencesEditor);
        when(mockSharedPreferencesEditor.commit()).thenReturn(true);

        authStateManager.save(null);

        verify(mockSharedPreferencesEditor, times(1)).remove(anyString());
    }

    @Test
    public void testSaveWithState() {
        when(mockOIDCCredentials.serialize()).thenReturn("TEST");
        when(mockSharedPreferencesEditor.putString(anyString(), anyString()))
                        .thenReturn(mockSharedPreferencesEditor);
        when(mockSharedPreferencesEditor.commit()).thenReturn(true);

        authStateManager.save(mockOIDCCredentials);

        verify(mockSharedPreferencesEditor, times(1)).putString(anyString(), eq("TEST"));
    }
}
