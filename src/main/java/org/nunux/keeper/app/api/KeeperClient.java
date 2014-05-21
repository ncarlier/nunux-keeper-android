package org.nunux.keeper.app.api;

import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Nunux Keeper client API.
 * @author Nicolas Carlier <n.carlier@nunux.org>
 */
public class KeeperClient {
    private static final String TAG = "SendActivity";

    private String url;
    private String accessToken;

    private AsyncHttpClient client;

    public KeeperClient(String url, String accessToken) {
        this.url = url;
        this.accessToken = accessToken;
        this.client = new AsyncHttpClient();
        this.client.addHeader("Authorization", "bearer " + accessToken);
    }

    public void postDocument(String title, String content, String contentType, AsyncHttpResponseHandler responseHandler) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("title", title));
        String paramString = URLEncodedUtils.format(params, "utf-8");

        HttpEntity entity;
        try {
            entity = new StringEntity(content);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unable to create http entity", e);
            return;
        }
        this.client.post(null, url + "/api/document?" + paramString, entity, contentType, responseHandler);
    }
}
