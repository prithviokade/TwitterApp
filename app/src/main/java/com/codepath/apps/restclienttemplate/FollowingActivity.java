
package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.databinding.ActivityFollowersBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class FollowingActivity extends AppCompatActivity {
    TextView tvFollowers;
    TextView tvFollowing;
    RecyclerView rvFollowing;
    FollowAdapter adapter;
    List<User> users;
    TwitterClient client;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        client = TwitterApplication.getRestClient(this);

        rvFollowing = findViewById(R.id.rvFollowing);
        users = new ArrayList<>();
        adapter = new FollowAdapter(this, users);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        rvFollowing.setLayoutManager(new LinearLayoutManager(this));
        rvFollowing.setAdapter(adapter);
        populateFollowing();

        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);

        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FollowingActivity.this, FollowersActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet)); // pass data
                startActivity(intent);
            }
        });

    }

    private void populateFollowing() {
        client.getFriends(tweet.user.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("FollowingActivity", "onSuccess " + json.toString());
                JSONObject jsonObject = json.jsonObject;
                try {
                    users.addAll(User.fromJsonArray(jsonObject.getJSONArray("users")));
                    Log.d("FollowingActivity", users.get(0).screenName);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("FollowingActivity", "JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("FollowingActivity", "onFailure " + response, throwable);
            }
        });
    }
}














