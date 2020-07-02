package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {

    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long userId;

    @Ignore
    public User user;

    @ColumnInfo
    public String entityUrl;

    @ColumnInfo
    public long retweets;

    @ColumnInfo
    public long favorites;

    @ColumnInfo
    public boolean isRetweeted;

    @ColumnInfo
    public boolean isFavorited;

    // empty constructor required by Parceler Library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Log.d("TweetHERE", jsonObject.toString());
        Tweet tweet = new Tweet();
        tweet.entityUrl = "";
        try {
            /*
            JSONObject entity = jsonObject.getJSONObject("entities");
            JSONArray urls = entity.getJSONArray("media");
            JSONObject main = urls.getJSONObject(0);
            tweet.entityUrl = main.getString("media_url_https");
            */
            tweet.entityUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
            if (tweet.entityUrl == null) {
                Log.d("TweetNull", "entityUrl is null");
            }
        } catch (JSONException e) {
            tweet.entityUrl = "";
        }
        tweet.id = jsonObject.getLong("id");
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.userId = tweet.user.id;
        tweet.retweets = jsonObject.getLong("retweet_count");
        tweet.favorites = jsonObject.getLong("favorite_count");
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");

        return tweet;
    }


    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static Tweet findOldest(List<Tweet> tweetsFromNetwork) {
        Tweet tweet = new Tweet();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            if (i == 0) {
                tweet = tweetsFromNetwork.get(i);
            } else if (tweetsFromNetwork.get(i).id < tweet.id) {
                tweet = tweetsFromNetwork.get(i);
            }
        }
        return tweet;
    }

    public String formatTime(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            String[] splitSpace = relativeDate.split(" ");
            relativeDate = splitSpace[0] + splitSpace[1].charAt(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}


