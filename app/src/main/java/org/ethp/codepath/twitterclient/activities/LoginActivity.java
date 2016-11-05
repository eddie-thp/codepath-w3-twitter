package org.ethp.codepath.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.twitterclient.R;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.application.AppConstants;
import org.ethp.codepath.twitterclient.models.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    private static final String LOG_TAG = "LoginActivity";

    TwitterClient mTwitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTwitterClient = TwitterApplication.getRestClient();
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        // Once we've successfully authenticated we retrieve the user
        // information from Twitter and launch the timeline activity
        mTwitterClient.getAuthenticatedUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(LOG_TAG, response.toString());
                try {
                    User authenticatedUser = User.fromJSONObject(response);
                    Intent timelineIntent = new Intent(LoginActivity.this, TimelineActivity.class);
                    timelineIntent.putExtra(AppConstants.EXTRA_USER, Parcels.wrap(authenticatedUser));
                    startActivity(timelineIntent);
                } catch (JSONException e) {
                    // TODO handle error
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(LOG_TAG, errorResponse.toString(), throwable);
                // TODO handle error
            }
        });
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        Log.e(LOG_TAG, "Authentication Failure", e);
        // TODO handle error
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        getClient().connect();
    }

}
