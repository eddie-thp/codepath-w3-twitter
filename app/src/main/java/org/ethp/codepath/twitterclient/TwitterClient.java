package org.ethp.codepath.twitterclient;

import android.content.Context;

import com.codepath.apps.twitterclient.R;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.ethp.codepath.twitterclient.models.Tweet;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.util.Map;

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
     * Gets tweets timeline from the different Twitter Api timeline endpoints
     *
     * @param apiResourceName
     * @param maxId
     * @param sinceId
     * @param extraParams
     * @param handler
     */
    public void getTweetsTimeline(String apiResourceName, long maxId, long sinceId, Map<String, String> extraParams, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/statuses/" + apiResourceName + ".json");
        //
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", sinceId);
        if (maxId != -1) {
            params.put("max_id", maxId);
        }
        // add extra parameters
        for (Map.Entry<String, String> entry: extraParams.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        //
        client.get(apiUrl, params, handler);
    }

    /**
     * Creates or destroys the favorite flag
     *
     * @param tweetId
     * @param favorited
     * @param handler
     */
    public void postFavorite(long tweetId, boolean favorited, AsyncHttpResponseHandler handler) {
        String apiResource = (favorited ? "destroy" : "create");

        String apiUrl = getApiUrl("/favorites/" + apiResource + ".json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        client.post(apiUrl, params, handler);
    }

    /**
     * Retweets or unretweets the tweet
     *
     * @param tweetId
     * @param retweeted
     * @param handler
     */
    public void postRetweet(long tweetId, boolean retweeted, AsyncHttpResponseHandler handler) {
        String apiResource = (retweeted ? "unretweet" : "retweet");

        String apiUrl = getApiUrl(String.format("/statuses/%s/%d.json", apiResource, tweetId));
        client.post(apiUrl, handler);
    }

    /**
     * Updates the authenticated user status (tweets)
     *
     * POST /statuses/update.json ? status=status
     *
     * @param status
     * @param handler
     */
    public void postStatus(String status, Tweet replyTo, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("/statuses/update.json");

        RequestParams params = new RequestParams();
        params.put("status", status);
        if (replyTo != null) {
            params.put("in_reply_to_status_id", replyTo.getUid());
        }

        client.post(apiUrl, params, handler);
    }
}