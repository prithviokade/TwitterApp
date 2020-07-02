package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvName;
    TextView tvRelDate;
    ImageView ivUrl;
    TextView tvRetweets;
    TextView tvFavs;
    ImageButton btnRetweet;
    ImageButton btnFav;
    ImageButton btnReply;

    TwitterClient client;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTweetDetailsBinding binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivProfile = binding.ivProfPic;
        tvBody = binding.tvBody;
        tvScreenName = binding.tvScreenName;
        tvName = binding.tvName;
        tvRelDate = binding.tvCreated;
        ivUrl = binding.ivUrl;
        tvRetweets = binding.tvRetweets;
        tvFavs = binding.tvLikes;
        btnFav = binding.btnFav;
        btnRetweet = binding.btnRetweet;
        btnReply = binding.btnReply;

        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.screenName);
        tvName.setText(tweet.user.name);
        tvRelDate.setText(tweet.formatTime(tweet.createdAt));
        tvRetweets.setText(Long.toString(tweet.retweets) + " Retweets");
        tvFavs.setText(Long.toString(tweet.favorites) + " Likes");

        if (tweet.isRetweeted) {
            btnRetweet.setColorFilter(Color.argb(255, 0, 211, 30)); // Green Tint
            tvRetweets.setTextColor(Color.argb(255, 0, 211, 30));
        } else {
            btnRetweet.setColorFilter(Color.argb(255, 170, 184, 194)); // Grey Tint
            tvRetweets.setTextColor(Color.argb(255, 170, 184, 194));
        }

        if (tweet.isFavorited) {
            btnFav.setColorFilter(Color.argb(255, 211, 0, 60)); // Red Tint
            tvFavs.setTextColor(Color.argb(255, 211, 184, 194));
        } else {
            btnFav.setColorFilter(Color.argb(255, 170, 184, 194)); // Grey Tint
            tvFavs.setTextColor(Color.argb(255, 170, 184, 194));
        }

        Glide.with(this).load(tweet.user.imageURL).transform(new CircleCrop()).into(ivProfile);
        if (!(tweet.entityUrl.isEmpty())) {
            ivUrl.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.entityUrl).into(ivUrl);
        } else {
            ivUrl.setVisibility(View.GONE);
        }
        client = TwitterApplication.getRestClient(this);

        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make API call on Twitter to post
                if (!tweet.isRetweeted) {
                    client.updateRetweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetsAdapter", "onSuccess to retweet");
                            btnRetweet.setColorFilter(Color.argb(255, 0, 211, 30)); // Green Tint
                            tweet.isRetweeted = true;
                            tweet.retweets++;
                            tvRetweets.setText(Long.toString(tweet.retweets) + " Retweets");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("TweetsAdapter", "onFailure to retweet" + response, throwable);
                        }
                    });
                } else {
                    client.updateUnRetweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetsAdapter", "onSuccess to unretweet");
                            btnRetweet.setColorFilter(Color.argb(255, 170, 184, 194)); // Grey Tint
                            tweet.isRetweeted = false;
                            tweet.retweets--;
                            tvRetweets.setText(Long.toString(tweet.retweets) + " Retweets");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("TweetsAdapter", "onFailure to unretweet" + response, throwable);
                        }
                    });
                }
            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make API call on Twitter to post
                if (!tweet.isFavorited) {
                    client.updateFavorite(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetsAdapter", "onSuccess to favorite");
                            btnFav.setColorFilter(Color.argb(255, 211, 0, 60)); // Red Tint
                            tweet.isFavorited = true;
                            tweet.favorites++;
                            tvFavs.setText(Long.toString(tweet.favorites)+ " Likes");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("TweetsAdapter", "onFailure to favorite" + response, throwable);
                        }
                    });
                } else {
                    client.updateUnFavorite(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetsAdapter", "onSuccess to unfavorite");
                            btnFav.setColorFilter(Color.argb(255, 170, 184, 194)); // Grey Tint
                            tweet.isFavorited = false;
                            tweet.favorites--;
                            tvFavs.setText(Long.toString(tweet.favorites) + " Likes");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("TweetsAdapter", "onFailure to unfavorite" + response, throwable);
                        }
                    });
                }
            }
        });

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TweetDetailsActivity.this, ReplyActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet)); // pass data
                startActivity(intent); // show activity
            }
        });

    }

}