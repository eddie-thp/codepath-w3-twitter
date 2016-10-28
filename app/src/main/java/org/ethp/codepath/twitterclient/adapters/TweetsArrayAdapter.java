package org.ethp.codepath.twitterclient.adapters;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.squareup.picasso.Picasso;

import org.ethp.codepath.twitterclient.models.Tweet;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.R.attr.resource;
import static com.codepath.apps.twitterclient.R.id.screen;
import static com.codepath.apps.twitterclient.R.id.tvText;

/**
 * Created by eddie_thp on 10/26/16.
 */

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Tweet tweet = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }

        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenNameRelativeTime);
        TextView tvText = (TextView) convertView.findViewById(R.id.tvText);

        ivProfileImage.setImageResource(0);
        tvUserName.setText(tweet.getUser().getName());

        String formattedScreenNameRelativeTime = formatScreenNameRelativeTime(tweet.getUser().getScreenName(),
                tweet.getCreatedAt());
        tvScreenName.setText(formattedScreenNameRelativeTime);

        tvText.setText(tweet.getText());

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).transform(new RoundedCornersTransformation(5, 0)).into(ivProfileImage);

        return convertView;
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
            Log.e("TWEETS_ARRAY_ADAPTER", "Failed to format relative date", e);
        }

        return formattedText;
    }
}
