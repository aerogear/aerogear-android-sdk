package org.aerogear.auth.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.aerogear.auth.credentials.OIDCCredentials;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthStateManagerTest {
    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private OIDCCredentials mockOIDCCredentials;
    @Mock
    private SharedPreferences.Editor mockSharedPreferencesEditor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor);
    }

    @Test
    public void testLoadWithEmptyStore() {
        AuthStateManager asm = new AuthStateManager(mockContext);
        OIDCCredentials authState = asm.load();

        assertNull(authState.getAccessToken());
        assertNull(authState.getIdentityToken());
        assertNull(authState.getRefreshToken());
    }

    @Test
    public void testSaveNull() throws JSONException {
        when(mockSharedPreferencesEditor.remove(anyString())).thenReturn(mockSharedPreferencesEditor);
        when(mockSharedPreferencesEditor.commit()).thenReturn(true);

        AuthStateManager asm = new AuthStateManager(mockContext);
        asm.save(null);

        verify(mockSharedPreferencesEditor, times(1)).remove(anyString());
    }

    @Test
    public void testSaveWithState() throws JSONException {
        when(mockOIDCCredentials.serialize()).thenReturn("TEST");
        when(mockSharedPreferencesEditor.putString(anyString(), anyString())).thenReturn(mockSharedPreferencesEditor);
        when(mockSharedPreferencesEditor.commit()).thenReturn(true);

        AuthStateManager asm = new AuthStateManager(mockContext);
        asm.save(mockOIDCCredentials);

        verify(mockSharedPreferencesEditor, times(1)).putString(anyString(), eq("TEST"));
    }
}
