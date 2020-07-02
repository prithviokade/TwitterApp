package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.widget.Toolbar;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    MenuItem miActionProgressItem;
    MenuItem compose;
    ProgressBar actionProgress;
    Toolbar toolbar;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    long start_id;

    TweetDao tweetDao;

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTimelineBinding binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.compose) {
                    // go to compose activity
                    Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    return true;
                }
                return false;
            }
        });
        Menu menu = toolbar.getMenu();
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        Log.d("miActionProgressItem", String.valueOf(miActionProgressItem));

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) binding.swipeContainer;
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        client = TwitterApplication.getRestClient(this);
        tweetDao = ((TwitterApplication) getApplicationContext()).getMyDatabase().tweetDao();

        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // for scroll listener
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        start_id = 0;
        // Init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // Set up layout manager and adapter of the recycler view
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("TimelineActivity", "scrolling");
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateNextHomeTimeline();
            }
        };

        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
        // Query for existing tweets in the database in a background thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing data from database");
                List<TweetWithUser> recentTweets = tweetDao.recentItems();
                List<Tweet> databaseTweets = TweetWithUser.getTweetList(recentTweets);
                adapter.clear();
                adapter.addAll(databaseTweets);
            }
        });
        populateHomeTimeline();
    }

    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }


     */
    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        compose = menu.findItem(R.id.compose);
        // actionProgress = menu.findItem(R.id.pbProgressAction);
        compose.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.d("TimelineActivityCompose", "woot woot");
                return true;
            }
        });

        return true; // so menu is displayed
    }

 */

/*
    public void composeItemClick() {
        Log.d("TimelineActivityCompose", "woot woot");
    }

 */

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // check if compose icon has been selected
        if (item.getItemId() == R.id.compose) {
            // go to compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
        return true; // consume the tap of the menu item
    }


 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // make sure requestCode is same as what we launched it with
        // make sure child activity (ComposeActivity) has finished successfully with resultCode
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // data is what child activity communicated back to us (added tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // update the RV with the data
            // modify data source of tweets to include this tweet
            tweets.add(0, tweet);
            // update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateNextHomeTimeline() {
        client.getNextHomeTimeline(tweets.get(tweets.size() - 1).id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccessNext " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                showProgressBar();
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    adapter.addAll(tweetsFromNetwork);
                    adapter.notifyItemRangeInserted(tweets.size() - tweetsFromNetwork.size() - 1, 25);
                    Log.d(TAG, tweets.toString());
                    // Tweet oldest_tweet = Tweet.findOldest(tweetsFromNetwork);
                    // start_id = oldest_tweet.id;
                    // Log.d("start_id", Long.toString(start_id));
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception", e);
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailureNext " + response, throwable);
            }
        });
        scrollListener.resetState();
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                showProgressBar();
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    adapter.clear();
                    adapter.addAll(tweetsFromNetwork);
                    Log.d(TAG, tweets.toString());
                    swipeContainer.setRefreshing(false);
                    Tweet oldest_tweet = Tweet.findOldest(tweetsFromNetwork);
                    start_id = oldest_tweet.id;

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Saving data to database");
                            // insert users
                            List<User> users = User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(users.toArray(new User[0]));
                            // then insert tweets
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception", e);
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure " + response, throwable);
            }
        });
    }

}