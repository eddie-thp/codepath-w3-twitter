package org.ethp.codepath.twitterclient.models;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Twitter User class
 */
// @Table(database = TwitterApplicationDatabase.class)
// @Parcel(analyze={User.class})
@Parcel
public class User extends BaseModel {
    // @PrimaryKey
    // @Column
    long uid;

    String name;
    String screenName;
    String profileImageUrl;

    /**
     * Empty constructor required by the Parceler library
     */
    public User() {
    }

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
