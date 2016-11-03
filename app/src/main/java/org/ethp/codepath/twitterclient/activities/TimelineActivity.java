package org.ethp.codepath.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterclient.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.fragments.ComposeTweetFragment;
import org.ethp.codepath.twitterclient.fragments.HomeTimelineFragment;
import org.ethp.codepath.twitterclient.fragments.MentionsTimelineFragment;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.models.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

/**
 * Tweet timeline activity, controls retrieval of Tweets timeline from the Twitter API
 */
public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnStatusUpdateListener {

    private static final String AUTHENTICATED_USER = "AuthUser";


    public class TweetsPageAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;

        private String titles[] = {"Home", "Mentions"};

        public TweetsPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else {
                return new MentionsTimelineFragment();
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

    private static final String LOG_TAG = "TimelineActivity";

    // Twitter REST Client implementation
    TwitterClient mTwitterClient;

    // Authenticated user
    User mAuthenticatedUser;

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

        ////
        // Get the view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the view pager adapter for the pager
        vpPager.setAdapter(new TweetsPageAdapter(getSupportFragmentManager()));
        // Find the pager slinding tabs
        PagerSlidingTabStrip tabString = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attache the pager tabstrip to the view pager
        tabString.setViewPager(vpPager);
        ////

        preInitializeMemberVariables();

        // Setup authenticated user
        getAuthenticatedUser();

        // Setup compose tweet button
        setupComposeTweetButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onProfileView(MenuItem mi) {
        Intent intent = new Intent(this, ProfileActivity.class);

        intent.putExtra(AUTHENTICATED_USER, Parcels.wrap(mAuthenticatedUser));
        startActivity(intent);
    }

    private void preInitializeMemberVariables() {
        // Member variables
        mTwitterClient = TwitterApplication.getRestClient();
        mAuthenticatedUser = null;
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

    /**
     * On ComposeTweetFragment StatusUpdate store new tweet into the timeline
     *
     * @param status
     */
    @Override
    public void onStatusUpdate(Tweet status) {
        // TODO uncomment, make it work with fragments
        // mTweets.add(0, status);
        // mTweetsAdapter.notifyItemInserted(0);
        // rvTweets.scrollToPosition(0);
        // TODO how to sync this with mSinceId and mMaxId
    }
}
