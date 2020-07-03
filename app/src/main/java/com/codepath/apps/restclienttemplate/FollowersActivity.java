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

public class FollowersActivity extends AppCompatActivity {

    TextView tvFollowers;
    TextView tvFollowing;
    RecyclerView rvFollowers;
    FollowAdapter adapter;
    List<User> users;
    TwitterClient client;
    Tweet tweet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        //ActivityFollowersBinding binding = ActivityFollowersBinding.inflate(getLayoutInflater());
        //View view = binding.getRoot();
        //setContentView(view);

        client = TwitterApplication.getRestClient(this);

        rvFollowers = findViewById(R.id.rvFollowers);
        users = new ArrayList<>();
        adapter = new FollowAdapter(this, users);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));


        rvFollowers.setLayoutManager(new LinearLayoutManager(this));
        rvFollowers.setAdapter(adapter);
        populateFollowers();

        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);


        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FollowersActivity.this, FollowingActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet)); // pass data
                startActivity(intent);
            }
        });



    }

    private void populateFollowers() {
        client.getFollowers(tweet.user.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("FollowersActivity", "onSuccess " + json.toString());
                JSONObject jsonObject = json.jsonObject;
                try {
                    users.addAll(User.fromJsonArray(jsonObject.getJSONArray("users")));
                    Log.d("FollowersActivity", users.get(0).screenName);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("FollowersActivity", "JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("FollowersActivity", "onFailure " + response, throwable);
            }
        });
    }
}