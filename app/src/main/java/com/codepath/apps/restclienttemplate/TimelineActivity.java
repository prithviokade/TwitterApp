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

    TweetDao tweetDao;

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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
        // Init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // Set up layout manager and adapter of the recycler view
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        // Query for existing tweets in the database in a background thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing data from database");
                List<TweetWithUser> recentTweets = tweetDao.recentItems();
                List<Tweet> databaseTweets = TweetWithUser.getTweetList(recentTweets);
                adapter.clear();
                adapter.addAll(databaseTweets); // sus
            }
        });

        populateHomeTimeline();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; // so menu is displayed
    }

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

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    adapter.clear();
                    adapter.addAll(tweetsFromNetwork);
                    Log.d(TAG, tweets.toString());
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);

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
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure " + response, throwable);
            }
        });
    }

}