package org.ethp.codepath.twitterclient.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Tweet model class
 */
@Parcel
public class Tweet {
    // Unique Id
    long uid;
    User user;
    String text;
    String createdAt;

    int favoriteCount;
    boolean favorited;

    int retweetCount;
    boolean retweeted;

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

    public int getRetweetCount() { return retweetCount; }

    public boolean isRetweeted() { return retweeted; }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public int getFavoriteCount() { return favoriteCount; }

    public boolean isFavorited() { return favorited; }

    public static Tweet fromJSONObject(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.uid = jsonObject.getLong("id");
        tweet.text = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");

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
