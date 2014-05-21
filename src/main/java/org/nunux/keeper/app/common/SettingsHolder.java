package org.nunux.keeper.app.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.nunux.keeper.app.store.SharedPreferencesCredentialStore;

/**
 * Settings holder.
 * @author Nicolas Carlier <n.carlier@nunux.org>
 */
public class SettingsHolder {

    /**
     * Private constructor.
     */
    private SettingsHolder() {
        super();
    }

    public String url;

    public String accessToken;

    /**
     * Get setting from the context.
     * @param context the context
     * @return settings
     */
    public static SettingsHolder get(Context context) {
        SettingsHolder settings = new SettingsHolder();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        settings.url = prefs.getString("url", null);

        SharedPreferencesCredentialStore credentialStore = new SharedPreferencesCredentialStore(prefs);
        settings.accessToken = credentialStore.getAccessToken(Constants.OAUTH2PARAMS.getUserId());

        return settings;
    }
}
