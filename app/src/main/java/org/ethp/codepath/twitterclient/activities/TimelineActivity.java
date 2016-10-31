package org.ethp.codepath.twitterclient.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.twitterclient.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.adapters.TweetsAdapter;
import org.ethp.codepath.twitterclient.fragments.ComposeTweetFragment;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.models.User;
import org.ethp.codepath.twitterclient.support.recyclerview.EndlessRecyclerViewScrollListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Tweet timeline activity, controls retrieval of Tweets timeline from the Twitter API
 */
public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnStatusUpdateListener {

    private static final String LOG_TAG = "TimelineActivity";

    // Twitter REST Client implementation
    TwitterClient mTwitterClient;

    // Stored index of the position that contains Tweet for sinceId and maxId
    // for Twitter REST API "pagination" control
    int mSinceIdIdx;
    int mMaxIdIdx;

    // Authenticated user
    User mAuthenticatedUser;

    // Tweets list and adapter
    TweetsAdapter mTweetsAdapter;
    List<Tweet> mTweets;

    // Layout view references
    SwipeRefreshLayout swipeContainer;
    RecyclerView rvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_timeline);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        preInitializeMemberVariables();

        // Setup authenticated user
        getAuthenticatedUser();

        // Setup recycler view with infinite scroll support
        setupTweetsRecyclerView();

        // Setup swipe refresh support
        setupSwipeRefresh();

        // Setup compose tweet button
        setupComposeTweetButton();

        // Fetch timeline
        fetchTimeline();
    }

    private void preInitializeMemberVariables() {
        // Member variables
        mTwitterClient = TwitterApplication.getRestClient();
        mSinceIdIdx = -1;
        mMaxIdIdx = -1;
        mAuthenticatedUser = null;
        mTweets = new ArrayList<>();
        mTweetsAdapter = new TweetsAdapter(this, mTweets);
        // Layout view references, could've used ButterKnife or DataBinding framework for this instead
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
    }

    private void getAuthenticatedUser() {
        mTwitterClient.getAuthenticatedUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(LOG_TAG, response.toString());
                try {
                    mAuthenticatedUser = User.fromJSONObject(response);
                } catch (JSONException e) {
                    // TODO handle error
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(LOG_TAG, errorResponse.toString());
                // TODO handle error
            }
        });

    }

    private void setupTweetsRecyclerView() {
        // Setup Timeline Tweets recycler view
        rvTweets.setAdapter(mTweetsAdapter);

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
                // This method is triggered when new data needs to be appended to the list
                fetchTimeline();
            }
        });


    }

    private void setupSwipeRefresh() {
        // Setup SwipeRefresh
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reset mMaxId
                mMaxIdIdx = -1;

                // In case we are already refreshing data, lets discard the older data from since
                if (mSinceIdIdx != -1) {
                    List<Tweet> endOfTheList = mTweets.subList(mSinceIdIdx, mTweets.size() - 1);
                    mTweets.removeAll(endOfTheList);
                    mTweetsAdapter.notifyItemRangeRemoved(mSinceIdIdx, endOfTheList.size());
                }

                // When refreshing the timeline, we need to pass the sinceId as explained in the Twitter API guidelines
                // https://dev.twitter.com/rest/public/timelines#using-since-id-for-the-greatest-efficiency
                // SinceId will be the 1st Tweet in our timeline
                mSinceIdIdx = 0;

                fetchTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupComposeTweetButton() {
        FloatingActionButton btSendTweet = (FloatingActionButton) findViewById(R.id.btSendTweet);
        btSendTweet.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment composeComposeTweetFragment = ComposeTweetFragment.newInstance(mAuthenticatedUser);
                composeComposeTweetFragment.show(fm, "fragment_send_tweet");
            }
        });
    }

    private long getSinceId() {
        long sinceId = 1;

        if (mSinceIdIdx != -1) {
            sinceId = mTweets.get(mSinceIdIdx).getUid();
        }

        return sinceId;
    }

    private long getMaxId() {
        long maxId = -1;

        if (mMaxIdIdx != -1) {
            // Subtracting 1 from the last tweet id as explained in the Twitter API guidelines
            // https://dev.twitter.com/rest/public/timelines#optimizing-max-id-for-environments-with-64-bit-integers
            // in order to prevent duplicated entries in the timeline
            // MaxId = Id of the Last Tweet - 1;
            maxId = (mTweets.get(mMaxIdIdx).getUid() - 1);
        }

        return maxId;
    }

    /**
     * Get Tweets list from the Tweet REST API
     */
    private void fetchTimeline() {
        long maxId = getMaxId();
        long sinceId = getSinceId();
        mTwitterClient.getHomeTimeline(maxId, sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Log.d(LOG_TAG, response.toString());
                Log.d(LOG_TAG, "BEFORE TotalTweets: " + mTweets.size() + " SINCE IDX: " + mSinceIdIdx + " MAX_IDX " + mMaxIdIdx);

                // Stop refreshing feedback
                swipeContainer.setRefreshing(false);
                // Retrieve new tweets from response
                List<Tweet> newTweets = Tweet.fromJSONArray(response);
                int newTweetsCount = newTweets.size();

                // In case we are not refreshing
                if (mSinceIdIdx == -1) {
                    // Append result to the end of the timeline and update mMaxIdIdx as being the last position of the timeline
                    int insertAt = mTweets.size();
                    mTweets.addAll(newTweets);
                    mTweetsAdapter.notifyItemRangeInserted(insertAt, newTweetsCount);
                    // Update mMaxIdIdx
                    mMaxIdIdx = mTweets.size() - 1;
                } else { // In case we are refreshing
                    // Insert the collection before sinceId Tweet
                    mTweets.addAll(mSinceIdIdx, newTweets);
                    mTweetsAdapter.notifyItemRangeInserted(mSinceIdIdx, newTweetsCount);
                    if (newTweetsCount == 25) {
                        // In case we receive a full page, we just update the sinceIdx
                        mSinceIdIdx += newTweetsCount;
                        // Update mMaxIdIdx
                        mMaxIdIdx = newTweetsCount - 1;
                    } else {
                        // Lets reset the since idx and the maxId
                        mSinceIdIdx = -1;
                        mMaxIdIdx = mTweets.size() - 1;
                    }
                }
                Log.d(LOG_TAG, "AFTER TotalTweets: " + mTweets.size() + " SINCE IDX: " + mSinceIdIdx + " MAX_IDX " + mMaxIdIdx);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                // D/DEBUG: {"errors":[{"code":44,"message":"since_id parameter is invalid."}]}

                // TODO handle error
            }
        });
    }

    /**
     * On ComposeTweetFragment StatusUpdate store new tweet into the timeline
     *
     * @param status
     */
    @Override
    public void onStatusUpdate(Tweet status) {
        mTweets.add(0, status);
        mTweetsAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
        // TODO how to sync this with mSinceId and mMaxId
    }
}
