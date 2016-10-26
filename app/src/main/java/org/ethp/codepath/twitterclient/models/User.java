package org.ethp.codepath.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eddie_thp on 10/26/16.
 */

public class User {
    String name;
    long uid;
    String screenName;
    String profileImageUrl;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJSONObject(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");
        return user;
    }
}
