package org.ethp.codepath.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.support.recyclerview.EndlessRecyclerViewScrollListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineFragment extends TweetsListFragment {

    private static final String LOG_TAG = "HomeTimelineFragment";

    // Twitter REST Client implementation
    TwitterClient mTwitterClient;

    // Stored index of the position that contains Tweet for sinceId and maxId
    // for Twitter REST API "pagination" control
    int mSinceIdIdx;
    int mMaxIdIdx;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize member variables
        mTwitterClient = TwitterApplication.getRestClient();
        mSinceIdIdx = -1;
        mMaxIdIdx = -1;
        // Fetch timeline
        fetchTimeline();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTweetsRecyclerView();
        setupSwipeRefresh();

    }


    private void setupTweetsRecyclerView() {
        // Setup Timeline Tweets recycler view
        rvTweets.setAdapter(mTweetsAdapter);

        // Setup RecyclerView layout manager and infinite scroll
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
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

    private long getSinceId() {
        long sinceId = 1;

        if (!mTweets.isEmpty() && mSinceIdIdx != -1) {
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
        mTwitterClient.getMentionsTimeline(maxId, sinceId, new JsonHttpResponseHandler() {
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
                    addAll(newTweets);
                    mMaxIdIdx = mTweets.size() - 1;
                } else {

                    addAll(mSinceIdIdx, newTweets);

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
}
