package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.databinding.ActivityReplyBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyActivity extends AppCompatActivity {

    Tweet tweet;
    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ReplyActivity";
    EditText etReply;
    Button btnReply;
    TextView tvCharLeft;
    TextView tvReplyingTo;
    ImageView ivProfile;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvName;
    TextView tvRelDate;
    ImageView ivUrl;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityReplyBinding binding = ActivityReplyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApplication.getRestClient(this);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivProfile = binding.ivProfPic;
        tvBody = binding.tvBody;
        tvScreenName = binding.tvScreenName;
        tvName = binding.tvName;
        tvRelDate = binding.tvRelDate;
        ivUrl = binding.ivUrl;

        etReply = binding.etReply;
        btnReply = binding.btnReply;
        tvCharLeft = binding.tvCharLeft;
        tvReplyingTo = binding.tvReplyingTo;

        tvReplyingTo.setText("Replying to " + tweet.user.screenName);
        etReply.setText(tweet.user.screenName + " ");
        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.screenName);
        tvName.setText(tweet.user.name);
        tvRelDate.setText("· " + tweet.getRelativeTimeAgo(tweet.createdAt));
        Glide.with(ReplyActivity.this).load(tweet.user.imageURL).transform(new CircleCrop()).into(ivProfile);
        if (!(tweet.entityUrl.isEmpty())) {
            ivUrl.setVisibility(View.VISIBLE);
            Glide.with(ReplyActivity.this).load(tweet.entityUrl).into(ivUrl);
        } else {
            ivUrl.setVisibility(View.GONE);
        }

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etReply.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ReplyActivity.this, "Your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ReplyActivity.this, "Your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                // Make API call on Twitter to post
                client.publishReply(tweet.id, tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published Tweet: " + tweet.body);
                            Intent data = new Intent();
                            data.putExtra("tweet", Parcels.wrap(tweet));
                            // set result code and bundle data for response
                            setResult(RESULT_OK, data);
                            finish(); // closes activity, passes data to parent
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });

        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCharLeft.setText(String.valueOf(MAX_TWEET_LENGTH - charSequence.length()));
                if (charSequence.length() > 0 && charSequence.length() <= MAX_TWEET_LENGTH) {
                    btnReply.setEnabled(true);
                    btnReply.setBackgroundColor(Color.argb(255, 29, 161, 242));
                } else {
                    btnReply.setEnabled(false);
                    btnReply.setBackgroundColor(Color.argb(255, 170, 184, 194));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

    }
}