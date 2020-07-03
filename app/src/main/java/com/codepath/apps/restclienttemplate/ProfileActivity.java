package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvScreenName;
    TextView tvName;
    TextView tvFollowers;
    TextView tvFollowing;
    TwitterClient client;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ivProfile = binding.ivProfPic;
        tvName = binding.tvName;
        tvScreenName = binding.tvScreenName;
        tvFollowers = binding.tvFollowers;
        tvFollowing = binding.tvFollowing;

        // Set up layout manager and adapter of the recycler view
        Glide.with(ProfileActivity.this).load(tweet.user.imageURL).transform(new CircleCrop()).into(ivProfile);
        tvName.setText(tweet.user.name);
        tvScreenName.setText(tweet.user.screenName);
        tvFollowers.setText(Long.toString(tweet.user.followers) + " Followers");
        tvFollowing.setText(Long.toString(tweet.user.following) + " Following");

        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet)); // pass data
                startActivity(intent);
            }
        });

        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FollowingActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet)); // pass data
                startActivity(intent);
            }
        });
    }
}