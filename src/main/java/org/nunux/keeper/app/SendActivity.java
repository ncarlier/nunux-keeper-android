package org.nunux.keeper.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;
import org.nunux.keeper.app.api.KeeperClient;
import org.nunux.keeper.app.common.Constants;
import org.nunux.keeper.app.common.SettingsHolder;


public class SendActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkConnectivity()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Unable to send data");
            alertDialog.setMessage("No active network is connected.");
            alertDialog.setIcon(R.drawable.ic_launcher);
            alertDialog.show();
            return;
        }

        SettingsHolder settings = SettingsHolder.get(this);

        if (TextUtils.isEmpty(settings.url)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Unable to send data");
            alertDialog.setMessage("Configuration is not set.");
            alertDialog.setIcon(R.drawable.ic_launcher);
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                }
            });
            alertDialog.show();
            return;
        }

        if (TextUtils.isEmpty(settings.accessToken)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Unable to send data");
            alertDialog.setMessage("Access token is not set.");
            alertDialog.setIcon(R.drawable.ic_launcher);
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getBaseContext(), OAuthAccessTokenActivity.class));
                }
            });
            alertDialog.show();
            return;
        }

        setContentView(R.layout.activity_send);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String text = extras.getString("android.intent.extra.TEXT");
            TextView statusText = (TextView)findViewById(R.id.statusText);
            statusText.setText(R.string.text_sending);
            statusText.setTextColor(getResources().getColor(R.color.white));
            Log.d(Constants.TAG, "Sending to "+ settings.url + " : " + text);

            KeeperClient client = new KeeperClient(settings.url, settings.accessToken);
            client.postDocument(null, text, "text/plain", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    TextView statusText = (TextView)findViewById(R.id.statusText);
                    statusText.setText(R.string.text_sent);
                    statusText.setTextColor(getResources().getColor(R.color.green));
                    Log.d(Constants.TAG, "Response : " + response.toString());
                }
                @Override
                public void onFailure(Throwable e, JSONObject errorResponse) {
                    TextView statusText = (TextView)findViewById(R.id.statusText);
                    statusText.setText(R.string.text_error);
                    statusText.setTextColor(getResources().getColor(R.color.red));
                    Log.e(Constants.TAG, "Response : " + errorResponse.toString(), e);
                }
            });
        }
    }

    public void cancelActivity(View v) {
        this.finish();
    }

    private boolean checkConnectivity() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
