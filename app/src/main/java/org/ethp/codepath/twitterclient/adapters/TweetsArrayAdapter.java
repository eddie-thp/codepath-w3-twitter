package org.ethp.codepath.twitterclient.adapters;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
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

import java.util.List;
import java.util.zip.Inflater;

import static android.R.attr.resource;

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
        TextView tvText = (TextView) convertView.findViewById(R.id.tvText);

        ivProfileImage.setImageResource(0);
        tvUserName.setText(tweet.getUser().getScreenName());
        tvText.setText(tweet.getText());

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        return convertView;
    }
}
