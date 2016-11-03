package org.ethp.codepath.twitterclient.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codepath.apps.twitterclient.R;

import org.ethp.codepath.twitterclient.fragments.UserTimelineFragment;
import org.ethp.codepath.twitterclient.models.User;
import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {

    private static final String AUTHENTICATED_USER = "AuthUser";

    User mAuthenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuthenticatedUser = Parcels.unwrap(getIntent().getParcelableExtra(AUTHENTICATED_USER));

        if (savedInstanceState == null) {

            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(mAuthenticatedUser);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }


    }
}
