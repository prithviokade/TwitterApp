package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {

    @PrimaryKey
    @ColumnInfo
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String screenName;

    @ColumnInfo
    public String imageURL;

    @ColumnInfo
    public long followers;

    @ColumnInfo
    public long following;

    // empty constructor required by Parceler Library
    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.id = jsonObject.getLong("id");
        user.name = jsonObject.getString("name");
        user.screenName = "@" + jsonObject.getString("screen_name");
        user.name = jsonObject.getString("name");
        user.imageURL = jsonObject.getString("profile_image_url_https");
        user.followers = jsonObject.getLong("followers_count");
        user.following = jsonObject.getLong("friends_count");
        return user;
    }

    public static List<User> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            users.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return users;

    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweets) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweets.size(); i++) {
            users.add(tweets.get(i).user);
        }
        return users;
    }
}
