package org.nunux.keeper.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.nunux.keeper.app.common.Constants;
import org.nunux.keeper.app.oauth.OAuth2Helper;

import java.net.URLDecoder;


public class OAuthAccessTokenActivity extends Activity {

    private OAuth2Helper oAuth2Helper;

    private WebView webview;

    boolean handled = false;

    private boolean hasLoggedIn;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Constants.TAG, "Starting task to retrieve request token.");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        oAuth2Helper = new OAuth2Helper(prefs);
        webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);
        //setContentView(R.layout.activity_oauth_access_token);

        String authorizationUrl = oAuth2Helper.getAuthorizationUrl();
        Log.i(Constants.TAG, "Using authorizationUrl = " + authorizationUrl);

        handled = false;

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap bitmap) {
                Log.d(Constants.TAG, "onPageStarted : " + url + " handled = " + handled);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                Log.d(Constants.TAG, "onPageFinished : " + url + " handled = " + handled);

                if (url.startsWith(Constants.OAUTH2PARAMS.getRedirectUri())) {
                    webview.setVisibility(View.INVISIBLE);

                    if (!handled) {
                        new ProcessToken(url).execute();
                    }
                } else {
                    webview.setVisibility(View.VISIBLE);
                }
            }

        });

        webview.loadUrl(authorizationUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "onResume called with " + hasLoggedIn);
        if (hasLoggedIn) {
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return (id == R.id.action_settings) || super.onOptionsItemSelected(item);
    }

    private class ProcessToken extends AsyncTask<Uri, Void, Void> {

        String url;
        boolean startActivity = false;


        public ProcessToken(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Uri... params) {
            if (url.startsWith(Constants.OAUTH2PARAMS.getRedirectUri())) {
                Log.i(Constants.TAG, "Redirect URL found" + url);
                handled = true;
                try {
                    if (url.contains("code=")) {
                        String authorizationCode = extractCodeFromUrl(url);

                        Log.i(Constants.TAG, "Found code = " + authorizationCode);

                        oAuth2Helper.retrieveAndStoreAccessToken(authorizationCode);
                        startActivity = true;
                        hasLoggedIn = true;

                    } else if (url.contains("error=")) {
                        startActivity = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Log.i(Constants.TAG, "Not doing anything for url " + url);
            }
            return null;
        }

        private String extractCodeFromUrl(String url) throws Exception {
            String encodedCode = url.substring(Constants.OAUTH2PARAMS.getRedirectUri().length() + 7, url.length());
            return URLDecoder.decode(encodedCode, "UTF-8");
        }

        @Override
        protected void onPreExecute() {

        }

        /**
         * When we're done and we've retrieved either a valid token or an error from the server,
         * we'll return to our original activity
         */
        @Override
        protected void onPostExecute(Void result) {
            if (startActivity) {
                Log.i(Constants.TAG, " ++++++++++++ Starting mainscreen again");
                startActivity(new Intent(OAuthAccessTokenActivity.this, MainActivity.class));
                finish();
            }
        }

    }
}
