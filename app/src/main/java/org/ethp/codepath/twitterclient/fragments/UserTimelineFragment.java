package org.ethp.codepath.twitterclient.fragments;

import android.os.Bundle;

import org.ethp.codepath.twitterclient.application.AppConstants;
import org.ethp.codepath.twitterclient.models.User;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

/**
 * User timeline fragment
 */
@TweetsTimeline(apiResourceName = "user_timeline")
public class UserTimelineFragment extends TweetsTimelineFragment {

    public UserTimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method instantiates a new ComposeTweetFragment for the authenticated user
     * @param authenticatedUser
     * @return
     */
    public static UserTimelineFragment newInstance(User authenticatedUser) {
        UserTimelineFragment fragment = new UserTimelineFragment();

        Bundle args = new Bundle();
        args.putParcelable(AppConstants.EXTRA_USER, Parcels.wrap(authenticatedUser));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected Map<String, String> getTimelineParameters() {
        Map<String, String> timelineParams = new HashMap<>();
        User user = Parcels.unwrap(getArguments().getParcelable(AppConstants.EXTRA_USER));
        timelineParams.put("screen_name", user.getScreenName());
        return timelineParams;
    }

}
