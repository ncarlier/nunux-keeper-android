package org.nunux.keeper.app.oauth;


import android.content.SharedPreferences;
import android.util.Log;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import org.nunux.keeper.app.common.Constants;
import org.nunux.keeper.app.store.SharedPreferencesCredentialStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class OAuth2Helper {

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private AuthorizationCodeFlow flow;

    private Oauth2Params oauth2Params;

    public OAuth2Helper(SharedPreferences sharedPreferences, Oauth2Params oauth2Params) {
        CredentialStore credentialStore = new SharedPreferencesCredentialStore(sharedPreferences);
        this.oauth2Params = oauth2Params;
        this.flow = new AuthorizationCodeFlow.Builder(oauth2Params.getAccessMethod(), HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl(oauth2Params.getTokenServerUrl()), new ClientParametersAuthentication(oauth2Params.getClientId(), oauth2Params.getClientSecret()), oauth2Params.getClientId(), oauth2Params.getAuthorizationServerEncodedUrl()).setCredentialStore(credentialStore).build();
    }

    public OAuth2Helper(SharedPreferences sharedPreferences) {
        this(sharedPreferences, Oauth2Params.KEEPER_OAUTH2);
    }

    public String getAuthorizationUrl() {
        return flow.newAuthorizationUrl().setRedirectUri(oauth2Params.getRedirectUri()).setScopes(convertScopesToString(oauth2Params.getScope())).build();
    }

    public void retrieveAndStoreAccessToken(String authorizationCode) throws IOException {
        Log.i(Constants.TAG, "retrieveAndStoreAccessToken for code " + authorizationCode);
        TokenResponse tokenResponse = flow.newTokenRequest(authorizationCode).setScopes(convertScopesToString(oauth2Params.getScope())).setRedirectUri(oauth2Params.getRedirectUri()).execute();
        Log.i(Constants.TAG, "Found tokenResponse :");
        Log.i(Constants.TAG, "Access Token : " + tokenResponse.getAccessToken());
        Log.i(Constants.TAG, "Refresh Token : " + tokenResponse.getRefreshToken());
        flow.createAndStoreCredential(tokenResponse, oauth2Params.getUserId());
    }

    public String executeApiCall() throws IOException {
        Log.i(Constants.TAG, "Executing API call at url " + this.oauth2Params.getApiUrl());
        return HTTP_TRANSPORT.createRequestFactory(loadCredential()).buildGetRequest(new GenericUrl(this.oauth2Params.getApiUrl())).execute().parseAsString();
    }

    public Credential loadCredential() throws IOException {
        return flow.loadCredential(oauth2Params.getUserId());
    }

    public void clearCredentials() throws IOException {
        flow.getCredentialStore().delete(oauth2Params.getUserId(), null);
    }

    private Collection<String> convertScopesToString(String scopesConcat) {
        String[] scopes = scopesConcat.split(",");
        Collection<String> collection = new ArrayList<String>();
        Collections.addAll(collection, scopes);
        return collection;
    }
}