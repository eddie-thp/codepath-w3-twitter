package org.ethp.codepath.twitterclient.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.squareup.picasso.Picasso;

import org.ethp.codepath.twitterclient.application.AppConstants;
import org.ethp.codepath.twitterclient.fragments.ComposeTweetFragment;
import org.ethp.codepath.twitterclient.fragments.UserTimelineFragment;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.models.User;
import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity implements ComposeTweetFragment.OnStatusUpdateListener {

    User mAuthenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuthenticatedUser = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.EXTRA_USER));

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("@" + mAuthenticatedUser.getScreenName());
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        populateProfileHeader();

        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(mAuthenticatedUser);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

    void populateProfileHeader() {
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvTagLine);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);

        tvName.setText(mAuthenticatedUser.getName());
        tvScreenName.setText(mAuthenticatedUser.getTagLine());
        tvFollowers.setText(mAuthenticatedUser.getFollowersCount() + " Followers");
        tvFollowing.setText(mAuthenticatedUser.getFriendsCount() + " Following");

        Picasso.with(this).load(mAuthenticatedUser.getProfileImageUrl()).into(ivProfileImage);
    }

    @Override
    public void onStatusUpdate(Tweet status) {
        // TODO if we reply to a tweet, what to do ?
    }
}
