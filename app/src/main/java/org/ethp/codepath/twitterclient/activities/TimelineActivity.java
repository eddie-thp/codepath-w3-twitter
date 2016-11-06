package org.ethp.codepath.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterclient.R;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.application.ActivityHelper;
import org.ethp.codepath.twitterclient.application.AppConstants;
import org.ethp.codepath.twitterclient.fragments.ComposeTweetFragment;
import org.ethp.codepath.twitterclient.fragments.HomeTimelineFragment;
import org.ethp.codepath.twitterclient.fragments.MentionsTimelineFragment;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.models.User;
import org.ethp.codepath.twitterclient.support.fragments.SmartFragmentStatePagerAdapter;
import org.parceler.Parcels;

/**
 * Tweet timeline activity, controls retrieval of Tweets timeline from the Twitter API
 */
public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnStatusUpdateListener {

    public class TweetsPageAdapter extends SmartFragmentStatePagerAdapter {
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

    // Adapters
    TweetsPageAdapter mTweetsPageAdapter;

    // Views
    ViewPager vpPager;
    PagerSlidingTabStrip psTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActivityHelper.setupSupportActionBar(this, R.id.toolbar, R.string.title_activity_timeline);

        preInitializeMemberVariables();

        setupViewPager();

        setupComposeTweetButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    private void preInitializeMemberVariables() {
        // Member variables
        mTwitterClient = TwitterApplication.getRestClient();
        mAuthenticatedUser = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.EXTRA_USER));
    }

    private void setupViewPager() {
        // Instantiate the adapter
        mTweetsPageAdapter = new TweetsPageAdapter(getSupportFragmentManager());
        // Setup view pager with TweetsPageAdapter
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(mTweetsPageAdapter);
        // Setup PagerSlidingTabString with ViewPager
        psTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        psTabStrip.setViewPager(vpPager);
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

    public void onProfileView(MenuItem mi) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(AppConstants.EXTRA_USER, Parcels.wrap(mAuthenticatedUser));
        startActivity(intent);
    }

    /**
     * On ComposeTweetFragment StatusUpdate store new tweet into the timeline
     *
     * @param status
     */
    @Override
    public void onStatusUpdate(Tweet status) {
        Fragment currentFragment = mTweetsPageAdapter.getRegisteredFragment(vpPager.getCurrentItem());

        if (currentFragment instanceof HomeTimelineFragment)
        {
            HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) currentFragment;
            homeTimelineFragment.addStatusUpdateTweet(status);
        }
    }
}
