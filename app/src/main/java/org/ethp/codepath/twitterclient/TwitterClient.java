package org.ethp.codepath.twitterclient;

import android.content.Context;

import com.codepath.apps.twitterclient.R;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * 
 * This is the object responsible for communicating with the Twitter REST API.
 */
public class TwitterClient extends OAuthBaseClient {
    // Twitter Rest API. See a full list of supported API classes:
    // https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // base API URL
	public static final String REST_CALLBACK_URL = "oauth://ethp-cp-tweets"; // Also defined in the manifest

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL,
				context.getString(R.string.rest_api_twitter_consumer_key),
				context.getString(R.string.rest_api_twitter_consumer_secret),
				REST_CALLBACK_URL);
	}

    /**
     * Gets the authenticated user information
     * GET /account/verify_credentials.json
     * @param handler
     */
    public void getAuthenticatedUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/account/verify_credentials.json");
        client.get(apiUrl, handler);
    }


    /**
     * Gets the latest 25 rows from the home timeline
     * GET /statuses/home_timeline.json ? count=25 & since_id=1
     * @param maxId
     * @param handler
     */
    public void getHomeTimeline(long maxId, long sinceId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/statuses/home_timeline.json");
        //
        RequestParams params = new RequestParams();
        params.put("count", 25);
        //
        params.put("since_id", sinceId);
        if (maxId != -1) {
            params.put("max_id", maxId);
        }
        //
        client.get(apiUrl, params, handler);
    }


    /**
     * Gets the latest 25 rows from the home timeline
     * GET /statuses/home_timeline.json ? count=25 & since_id=1
     * @param maxId
     * @param handler
     */
    public void getMentionsTimeline(long maxId, long sinceId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/statuses/mentions_timeline.json");
        //
        RequestParams params = new RequestParams();
        params.put("count", 25);
        //
        params.put("since_id", sinceId);
        if (maxId != -1) {
            params.put("max_id", maxId);
        }
        //
        client.get(apiUrl, params, handler);
    }

    /**
     * Updates the authenticated user status (tweets)
     *
     * POST /statuses/update.json ? status=status
     *
     * @param status
     * @param handler
     */
    public void postStatus(String status, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("/statuses/update.json");

        RequestParams params = new RequestParams();
        params.put("status", status);

        client.post(apiUrl, params, handler);
    }
}