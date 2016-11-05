package org.ethp.codepath.twitterclient.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.application.AppConstants;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.models.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.R.attr.editable;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Compose tweet fragment
 */
public class ComposeTweetFragment extends DialogFragment {

    /**
     * This interface must be implemented by activities that contain this
     * fragment to receive the composed tweet back
     *
     * See the Android Training lesson
     * <a href="http://developer.android.com/training/basics/fragments/communicating.html">Communicating with Other Fragments</a> for more information.
     */
    public interface OnStatusUpdateListener {
        void onStatusUpdate(Tweet status);
    }

    private OnStatusUpdateListener mStatusUpdateListener;

    TwitterClient mTwitterClient;
    User mAuthenticatedUser;
    private int mTweetMaxLength;
    private Tweet mTweet;

    ImageButton ibCloseFragment;
    ImageView ivAuthenticatedUserProfile;
    EditText etTweet;
    TextView tvTweetSize;
    Button btTweet;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method instantiates a new ComposeTweetFragment for the authenticated user
     * @param authenticatedUser
     * @return
     */
    public static ComposeTweetFragment newInstance(User authenticatedUser) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();

        Bundle args = new Bundle();
        args.putParcelable(AppConstants.EXTRA_USER, Parcels.wrap(authenticatedUser));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve arguments and initialize class attributes
        if (getArguments() != null) {
            mAuthenticatedUser = Parcels.unwrap(getArguments().getParcelable(AppConstants.EXTRA_USER));
        }
        mTwitterClient = TwitterApplication.getRestClient();
        mTweetMaxLength = getResources().getInteger(R.integer.tweet_max_length);
        mTweet = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View tweetView = inflater.inflate(R.layout.fragment_compose_tweet, container, false);

        ibCloseFragment = (ImageButton) tweetView.findViewById(R.id.ivClose);
        ivAuthenticatedUserProfile = (ImageView) tweetView.findViewById(R.id.ivProfileImage);
        etTweet = (EditText) tweetView.findViewById(R.id.etTweet);
        tvTweetSize = (TextView) tweetView.findViewById(R.id.tvMessageSize);
        btTweet = (Button) tweetView.findViewById(R.id.btTweet);

        Picasso.with(getContext()).load(mAuthenticatedUser.getProfileImageUrl()).transform(new RoundedCornersTransformation(5, 0)).into(ivAuthenticatedUserProfile);

        ibCloseFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComposeTweetFragment.this.dismiss();
            }
        });


        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int before) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                int remainingLength = mTweetMaxLength - charSequence.length();
                tvTweetSize.setText(String.valueOf(remainingLength));

                boolean btTweetEnabled = true;
                int color = android.R.color.tertiary_text_dark;
                if (remainingLength < 0) {
                    btTweetEnabled = false;
                    color  = android.R.color.holo_red_light;
                }
                tvTweetSize.setTextColor(ContextCompat.getColor(getContext(), color));
                btTweet.setEnabled(btTweetEnabled);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable statusText = etTweet.getText();
                if (statusText.length() > 0) {
                    mTweet = null;
                    mTwitterClient.postStatus(statusText.toString(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // TODO return tweet
                            Log.d("DEBUG", response.toString());
                            try {
                                mTweet = Tweet.fromJSONObject(response);

                                mStatusUpdateListener.onStatusUpdate(mTweet);

                                ComposeTweetFragment.this.dismiss();
                            } catch (JSONException e) {
                                // TODO handle error
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                            // TODO handle error
                        }
                    });
                }
            }
        });

        return tweetView;
    }

    /**
     * Modify dialog to occupy 90% of the screen
     */
    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 90% of the screen width/height
        window.setLayout((int) (size.x * 0.90), (int) (size.y * 0.90));
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Store a reference to the OnStatusUpdateListener
        if (context instanceof OnStatusUpdateListener) {
            mStatusUpdateListener = (OnStatusUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnStatusUpdateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Remove reference to the OnStatusUpdateListener
        mStatusUpdateListener = null;
        mTweet = null;
    }
}
