package org.ethp.codepath.twitterclient.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterclient.R;

import org.ethp.codepath.twitterclient.adapters.TweetsAdapter;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.support.recyclerview.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import static com.codepath.apps.twitterclient.R.id.rvTweets;
import static com.codepath.apps.twitterclient.R.id.swipeContainer;

public class TweetsListFragment extends Fragment {

    // Tweets list and adapter
    TweetsAdapter mTweetsAdapter;
    List<Tweet> mTweets;

    // References to the layout views
    SwipeRefreshLayout swipeContainer;
    RecyclerView rvTweets;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize member variables
        mTweets = new ArrayList<>();
        mTweetsAdapter = new TweetsAdapter(getActivity(), mTweets);
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

}
