package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {

    @Embedded // embedded notation flattens the properties of the User object into the object
    User user;

    @Embedded(prefix = "tweet_") // to avoid id vars overlapping
    Tweet tweet;

    public static List<Tweet> getTweetList(List<TweetWithUser> recentTweets) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < recentTweets.size(); i++) {
            Tweet tweet = new Tweet();
            tweet.user = recentTweets.get(i).user;
            tweets.add(tweet);
        }
        return tweets;
    }




}
