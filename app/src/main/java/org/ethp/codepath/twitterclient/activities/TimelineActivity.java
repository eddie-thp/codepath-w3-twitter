package org.ethp.codepath.twitterclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.twitterclient.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.adapters.TweetsAdapter;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient twitterClient;
    TweetsAdapter tweetsAdapter;
    List<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApplication.getRestClient();

        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);

        // Setup Timeline Tweets recycler view
        RecyclerView rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        rvTweets.setAdapter(tweetsAdapter);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        populateTimeline();
    }

    // Send API request and creates tweets from json
    private void populateTimeline() {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                  Log.d("DEBUG", response.toString());
                  tweets.addAll(Tweet.fromJSONArray(response));
                  tweetsAdapter.notifyItemRangeInserted(0, tweets.size());
              }

              @Override
              public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("DEBUG" , errorResponse.toString());
                  // TODO handle error
              }
          });
    }
}
