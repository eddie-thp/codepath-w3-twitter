package org.ethp.codepath.twitterclient.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.ethp.codepath.twitterclient.TwitterApplicationDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import static com.raizlabs.android.dbflow.sql.language.SQLite.select;

/**
 * Twitter User class
 */
@Table(database = TwitterApplicationDatabase.class)
@Parcel(analyze={User.class})
public class User extends BaseModel {

    @PrimaryKey
    @Column
    long uid;

    @Column
    String name;

    @Column
    String screenName;

    @Column
    String profileImageUrl;

    @Column
    String description;

    @Column
    int followersCount;

    @Column
    int friendsCount;

    @Column
    boolean authenticatedUser;

    /**
     * Empty constructor required by the Parceler library
     */
    public User() {
        authenticatedUser = false;
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

    public String getTagLine() { return description; }

    public int getFollowersCount() { return followersCount; }

    public int getFriendsCount() { return friendsCount; }

    public boolean isAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(boolean authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public static User fromJSONObject(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");
        user.description = jsonObject.getString("description");
        user.followersCount = jsonObject.getInt("followers_count");
        user.friendsCount = jsonObject.getInt("friends_count");

        return user;
    }

    public static User authenticatedUserFromDb() {
        return SQLite.select().from(User.class).where(User_Table.authenticatedUser.is(true)).querySingle();
    }
}
