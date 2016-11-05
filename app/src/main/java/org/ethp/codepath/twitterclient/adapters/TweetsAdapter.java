package org.ethp.codepath.twitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.squareup.picasso.Picasso;

import org.ethp.codepath.twitterclient.activities.ProfileActivity;
import org.ethp.codepath.twitterclient.application.AppConstants;
import org.ethp.codepath.twitterclient.models.Tweet;
import org.ethp.codepath.twitterclient.models.User;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.codepath.apps.twitterclient.R.id.tvName;

/**
 * Tweets timeline RecyclerView adapter implementation
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.TweetViewHolder> {

    private static final String LOG_TAG = "TweetsAdapter";

    /**
     * Holds a reference to the Tweet Item layout widgets
     */
    public class TweetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context mContext;
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvText;

        /**
         * Constructor
         * @param itemView
         */
        public TweetViewHolder(View itemView) {
            super(itemView);

            mContext = getContext();

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenNameRelativeTime);
            tvText = (TextView) itemView.findViewById(R.id.tvText);

            if (!(mContext instanceof ProfileActivity)) {
                ivProfileImage.setOnClickListener(this);
            }
        }

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
    }

    Context context;

    List<Tweet> tweets;

    /**
     * TweetsAdapter Constructor
     * @param context
     * @param tweets
     */
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    /**
     * Returns the Context object
     * @return Context
     */
    private Context getContext() {
        return context;
    }

    /**
     * Creates the Tweet Item view holder object
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
