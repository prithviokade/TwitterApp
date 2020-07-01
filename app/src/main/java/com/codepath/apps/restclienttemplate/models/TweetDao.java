package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao // Data Access Object
public interface TweetDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.id AS tweet_id, Tweet.entityUrl AS tweet_entityUrl, " +
            "Tweet.retweets AS tweet_retweets, Tweet.favorites AS tweet_favorites, Tweet.isFavorited AS tweet_isFavorited, Tweet.isRetweeted as tweet_isRetweeted, " +
            "User.* FROM Tweet INNER JOIN User ON Tweet.userId = User.id ORDER BY Tweet.createdAt DESC LIMIT 300")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
