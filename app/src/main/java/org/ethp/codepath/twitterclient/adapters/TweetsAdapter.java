package org.ethp.codepath.twitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.ethp.codepath.twitterclient.TwitterApplication;
import org.ethp.codepath.twitterclient.TwitterClient;
import org.ethp.codepath.twitterclient.activities.ProfileActivity;
import org.ethp.codepath.twitterclient.application.AppConstants;
import org.ethp.codepath.twitterclient.fragments.ComposeTweetFragment;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.models.User;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.codepath.apps.twitterclient.R.id.ivRetweet;

/**
 * Tweets timeline RecyclerView adapter implementation
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.TweetViewHolder> {

    private static final String LOG_TAG = "TweetsAdapter";

    TwitterClient mTwitterClient;

    /**
     * Holds a reference to the Tweet Item layout widgets
     */
    public class TweetViewHolder extends RecyclerView.ViewHolder {

        Context mContext;
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvText;
        ImageView ivReply;
        ImageView ivRetweet;
        TextView tvRetweetCount;
        ImageView ivFavorite;
        TextView tvFavoriteCount;
        ImageView ivShare;

        /**
         * Constructor
         *
         * @param itemView
         */
        public TweetViewHolder(View itemView) {
            super(itemView);

            mContext = getContext();

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenNameRelativeTime);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);
            tvFavoriteCount = (TextView) itemView.findViewById(R.id.tvFavoriteCount);
            ivShare = (ImageView) itemView.findViewById(R.id.ivShare);

            if (!(mContext instanceof ProfileActivity)) {
                setupOnProfileClickListener();
            }

            setupOnReplyClickListener();
            setupOnFavoriteClickListener();
        }

        private void setupOnReplyClickListener() {
            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    // Check if an item was deleted, but the user clicked it before the UI removed it
                    if (position != RecyclerView.NO_POSITION) {
                        Tweet replyToTweet = tweets.get(position);
                        FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
                        ComposeTweetFragment composeComposeTweetFragment = ComposeTweetFragment.newInstance(User.authenticatedUserFromDb(), replyToTweet);
                        composeComposeTweetFragment.show(fm, "fragment_send_tweet");
                    }
                }
            });
        }

        private void setupOnProfileClickListener() {
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    // Check if an item was deleted, but the user clicked it before the UI removed it
                    if (position != RecyclerView.NO_POSITION) {
                        Tweet tweet = tweets.get(position);
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra(AppConstants.EXTRA_USER, Parcels.wrap(tweet.getUser()));
                        mContext.startActivity(intent);
                    }

                }
            });
        }

        public void setupOnFavoriteClickListener() {
            int position = getAdapterPosition();

            // Check if an item was deleted, but the user clicked it before the UI removed it
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                mTwitterClient.postFavorite(tweet.getUid(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // TODO
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // TODO
                    }
                });
            }
        }

    }

    Context context;

    List<Tweet> tweets;

    /**
     * TweetsAdapter Constructor
     *
     * @param context
     * @param tweets
     */
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        mTwitterClient = TwitterApplication.getRestClient();
        this.context = context;
        this.tweets = tweets;
    }

    /**
     * Returns the Context object
     *
     * @return Context
     */
    private Context getContext() {
        return context;
    }

    /**
     * Creates the Tweet Item view holder object
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the Tweet Item layout and return the view holder
        View contactView = inflater.inflate(R.layout.item_tweet, parent, false);

        return (new TweetViewHolder(contactView));
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        holder.ivProfileImage.setImageResource(0);
        holder.tvUserName.setText(tweet.getUser().getName());
        String formattedScreenNameRelativeTime = formatScreenNameRelativeTime(tweet.getUser().getScreenName(),
                tweet.getCreatedAt());
        holder.tvScreenName.setText(formattedScreenNameRelativeTime);
        holder.tvText.setText(tweet.getText());

        Resources res = getContext().getResources();

        // Set the retweet color
        int retweetColor = (tweet.isRetweeted() ? R.color.twitterRetweetedIcon : R.color.twitterItemIcons);
        holder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        holder.tvRetweetCount.setTextColor(res.getColor(retweetColor));
        DrawableCompat.setTint(holder.ivRetweet.getDrawable(), res.getColor(retweetColor));

        // Set the favorite color
        int favoriteColor = (tweet.isFavorited() ? R.color.twitterFavoritedIcon : R.color.twitterItemIcons);
        holder.tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        holder.tvFavoriteCount.setTextColor(res.getColor(favoriteColor));
        DrawableCompat.setTint(holder.ivFavorite.getDrawable(), res.getColor(favoriteColor));

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).transform(new RoundedCornersTransformation(5, 0)).into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    private String formatScreenNameRelativeTime(String screenName, String createdAt) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String formattedText = screenName;
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            formattedText = String.format("@%s - %s", screenName,
                    DateUtils.getRelativeTimeSpanString(dateMillis,
                            System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString());
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Failed to format relative date", e);
        }

        return formattedText;
    }
}
