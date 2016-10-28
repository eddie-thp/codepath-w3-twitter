package org.ethp.codepath.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.twitterclient.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.adapters.TweetsAdapter;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.support.recyclerview.EndlessRecyclerViewScrollListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient twitterClient;

    SwipeRefreshLayout swipeContainer;
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

        // Setup RecyclerView layout manager and infinite scroll
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);

        // Setup RecyclerView endless scroll
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                long maxId = tweets.get(tweets.size() - 1).getUid();
                populateTimeline(maxId);
            }
        });

        // Setup SwipeRefresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                // TODO fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        populateTimeline(0);
    }

    // Send API request and creates tweets from json
    private void populateTimeline(long maxId) {
        twitterClient.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                  Log.d("DEBUG", response.toString());
                  List<Tweet> tweetsToAdd = Tweet.fromJSONArray(response);
                  int insertAt = tweets.size();
                  tweets.addAll(tweetsToAdd);
                  tweetsAdapter.notifyItemRangeInserted(insertAt, tweetsToAdd.size());
              }

              @Override
              public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                  Log.d("DEBUG" , errorResponse.toString());
                  // TODO handle error
              }
          });
    }
}
