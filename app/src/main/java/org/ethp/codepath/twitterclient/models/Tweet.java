package org.ethp.codepath.twitterclient.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Tweet model class
 */
public class Tweet {
    // Unique Id
    long uid;
    User user;
    String text;
    String createdAt;

    public long getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public static Tweet fromJSONObject(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.uid = jsonObject.getLong("id");
        tweet.text = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSONObject(jsonObject.getJSONObject("user"));

        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                tweets.add(fromJSONObject(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                Log.e("MODEL_TWEET", "Failed to parse Tweets json array", e);
            }
        }
        return tweets;
    }
}
