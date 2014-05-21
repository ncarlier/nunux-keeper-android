package org.nunux.keeper.app.oauth;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;

public enum Oauth2Params {

    KEEPER_OAUTH2(
            KeeperConstants.CLIENT_ID,
            KeeperConstants.CLIENT_SECRET,
            KeeperConstants.TOKEN_SERVER_URL,
            KeeperConstants.AUTHORIZATION_SERVER_URL,
            BearerToken.authorizationHeaderAccessMethod(),
            "*",
            KeeperConstants.REDIRECT_URL,
            "keeperApp",
            ""
    );

    private String clientId;
    private String clientSecret;
    private String scope;
    private String redirectUri;
    private String userId;
    private String apiUrl;

    private String tokenServerUrl;
    private String authorizationServerEncodedUrl;

    private AccessMethod accessMethod;

    Oauth2Params(String clientId, String clientSecret, String tokenServerUrl, String authorizationServerEncodedUrl, AccessMethod accessMethod, String scope, String redirectUri, String userId, String apiUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenServerUrl = tokenServerUrl;
        this.authorizationServerEncodedUrl = authorizationServerEncodedUrl;
        this.accessMethod = accessMethod;
        this.scope = scope;
        this.redirectUri = redirectUri;
        this.userId = userId;
        this.apiUrl = apiUrl;
    }

    public String getClientId() {
        if (this.clientId == null || this.clientId.length() == 0) {
            throw new IllegalArgumentException("Please provide a valid clientId in the Oauth2Params class");
        }
        return clientId;
    }

    public String getClientSecret() {
        if (this.clientSecret == null || this.clientSecret.length() == 0) {
            throw new IllegalArgumentException("Please provide a valid clientSecret in the Oauth2Params class");
        }
        return clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getTokenServerUrl() {
        return tokenServerUrl;
    }

    public String getAuthorizationServerEncodedUrl() {
        return authorizationServerEncodedUrl;
    }

    public AccessMethod getAccessMethod() {
        return accessMethod;
    }

    public String getUserId() {
        return userId;
    }
}