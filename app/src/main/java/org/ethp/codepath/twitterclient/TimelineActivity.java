package org.ethp.codepath.twitterclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.codepath.apps.twitterclient.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.ethp.codepath.twitterclient.adapters.TweetsArrayAdapter;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient twitterClient;
    TweetsArrayAdapter tweetsArrayAdapter;
    List<Tweet> tweets;
    ListView lvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApplication.getRestClient();

        tweets = new ArrayList<>();
        tweetsArrayAdapter = new TweetsArrayAdapter(this, tweets);

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        lvTweets.setAdapter(tweetsArrayAdapter);

        populateTimeline();
    }

    // Send API request and creates tweets from json
    private void populateTimeline() {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                  Log.d("DEBUG", response.toString());
                  tweets.addAll(Tweet.fromJSONArray(response));
                  tweetsArrayAdapter.notifyDataSetChanged();
              }

              @Override
              public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("DEBUG" , errorResponse.toString());
                  // TODO handle error
              }
          });
    }
}
