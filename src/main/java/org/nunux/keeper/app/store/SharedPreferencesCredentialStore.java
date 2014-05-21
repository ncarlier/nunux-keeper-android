package org.nunux.keeper.app.store;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import org.nunux.keeper.app.common.Constants;

import java.io.IOException;

/**
 * Simple CredentialStore for the Android platform that stores the credentials in
 * shared preferences.
 *
 * @author davydewaele
 */
public class SharedPreferencesCredentialStore implements CredentialStore {

    private static final String ACCESS_TOKEN = "_access_token";
    private static final String EXPIRES_IN = "_expires_in";
    private static final String REFRESH_TOKEN = "_refresh_token";
    private static final String SCOPE = "_scope";

    private SharedPreferences prefs;

    public SharedPreferencesCredentialStore(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    /**
     * Get access token.
     * @param userId user id
     * @return access token
     */
    public String getAccessToken(String userId) {
        return prefs.getString(userId + ACCESS_TOKEN, "");
    }

    /**
     * Load a credential from our shared preferences.
     */
    public boolean load(String userId, Credential credential)
            throws IOException {
        Log.i(Constants.TAG, "Loading credential for userId " + userId);
        Log.i(Constants.TAG, "Loaded access token = " + prefs.getString(userId + ACCESS_TOKEN, ""));

        credential.setAccessToken(prefs.getString(userId + ACCESS_TOKEN, null));

        if (prefs.contains(userId + EXPIRES_IN)) {
            credential.setExpirationTimeMilliseconds(prefs.getLong(userId + EXPIRES_IN, 0));
        }
        credential.setRefreshToken(prefs.getString(userId + REFRESH_TOKEN, null));

        return true;
    }

    /**
     * When storing credentials, we always expect to find an accessToken
     * and optionally a refresh token + expiration.
     * <p/>
     * Foursquare for example only returns a long-lived access token without a refresh token (and without an expiration).
     * <p/>
     * Google APIs usually return both an access token, refresh token and expiration info.
     */
    public void store(String userId, Credential credential) throws IOException {
        Log.i(Constants.TAG, "Storing credential for userId " + userId);
        Log.i(Constants.TAG, "Access Token = " + credential.getAccessToken());
        Editor editor = prefs.edit();

        editor.putString(userId + ACCESS_TOKEN, credential.getAccessToken());

        if (credential.getExpirationTimeMilliseconds() != null) {
            editor.putLong(userId + EXPIRES_IN, credential.getExpirationTimeMilliseconds());
        }

        if (credential.getRefreshToken() != null) {
            editor.putString(userId + REFRESH_TOKEN, credential.getRefreshToken());
        }
        editor.commit();
    }

    /**
     * Clearing all token related info from our shared preferences.
     */
    public void delete(String userId, Credential credential) throws IOException {
        Log.i(Constants.TAG, "Deleting credential for userId " + userId);
        Editor editor = prefs.edit();
        editor.remove(userId + ACCESS_TOKEN);
        editor.remove(userId + EXPIRES_IN);
        editor.remove(userId + REFRESH_TOKEN);
        editor.remove(userId + SCOPE);
        editor.commit();
    }
}