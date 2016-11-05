package org.ethp.codepath.twitterclient.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterclient.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.adapters.TweetsAdapter;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.support.recyclerview.EndlessRecyclerViewScrollListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.twitterclient.R.id.rvTweets;
import static com.codepath.apps.twitterclient.R.id.swipeContainer;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@interface TweetsTimeline {
    String apiResourceName();
}

public abstract class TweetsTimelineFragment extends Fragment {

    private static final String LOG_TAG = "TweetsTimelineFragment";

    // Twitter REST Client implementation
    TwitterClient mTwitterClient;
    String mApiResourceName;

    // Tweets list and adapter
    TweetsAdapter mTweetsAdapter;
    List<Tweet> mTweets;

    // References to the layout views
    SwipeRefreshLayout swipeContainer;
    RecyclerView rvTweets;

    // Stored index of the position that contains Tweet for sinceId and maxId
    // for Twitter REST API "pagination" control
    int mSinceIdIdx;
    int mMaxIdIdx;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        TweetsTimeline tweetTimelineAnnotation = this.getClass().getAnnotation(TweetsTimeline.class);

        if (tweetTimelineAnnotation == null) {
            throw new RuntimeException("Class must declare TweetsTimeline annotation.");
        }

        mApiResourceName = tweetTimelineAnnotation.apiResourceName();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize member variables
        mTwitterClient = TwitterApplication.getRestClient();

        // initialize member variables
        mSinceIdIdx = -1;
        mMaxIdIdx = -1;

        mTweets = new ArrayList<>();
        mTweetsAdapter = new TweetsAdapter(getActivity(), mTweets);

        // Fetch timeline
        fetchTimeline();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        // Initialize references to the layout views, could've used ButterKnife or DataBinding framework for this instead
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        rvTweets = (RecyclerView) view.findViewById(R.id.rvTweets);
        return view;
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

    protected long getSinceId() {
        long sinceId = 1;

        if (!mTweets.isEmpty() && mSinceIdIdx != -1) {
            sinceId = mTweets.get(mSinceIdIdx).getUid();
        }

        return sinceId;
    }

    protected long getMaxId() {
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



    public void addAll(List<Tweet> tweets) {
        int insertAt = mTweets.size();
        mTweets.addAll(tweets);
        mTweetsAdapter.notifyItemRangeInserted(insertAt, tweets.size());

    }

    public void addAll(int index, List<Tweet> tweets) {
        mTweets.addAll(index, tweets);
        mTweetsAdapter.notifyItemRangeInserted(index, tweets.size());

        // In case we are refreshing, scroll to beginning of the list
        if(index == 0) {
            rvTweets.scrollToPosition(0);
        }

    }

    protected Map<String, String> getTimelineParameters() {
        return new HashMap<>();
    }

    /**
     * Get Tweets list from the Tweet REST API
     */
    private void fetchTimeline() {


        Map<String, String> timelineParameters = getTimelineParameters();

        long maxId = getMaxId();
        long sinceId = getSinceId();
        mTwitterClient.getTweetsTimeline(mApiResourceName, maxId, sinceId, timelineParameters, new JsonHttpResponseHandler() {
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
                // {"errors":[{"code":135,"message":"Timestamp out of bounds."}]}

                // TODO handle error
            }
        });
    }

}
