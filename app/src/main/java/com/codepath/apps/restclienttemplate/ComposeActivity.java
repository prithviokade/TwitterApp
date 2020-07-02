package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends DialogFragment {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeActivity";

    EditText etCompose;
    Button btnTweet;
    TextView tvCharLeft;

    TwitterClient client;

    public ComposeActivity() {}

    public static ComposeActivity newInstance(){
        ComposeActivity frag = new ComposeActivity();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient(this);

        etCompose = (EditText) view.findViewById(R.id.etCompose);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        tvCharLeft = (TextView) view.findViewById(R.id.tvCharLeft);

        // Set click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    btnTweet.setEnabled(false);
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    btnTweet.setEnabled(false);
                    return;
                }
                btnTweet.setEnabled(true);
                // Make API call on Twitter to post
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
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
                            dismiss(); // closes activity, passes data to parent
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

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCharLeft.setText(String.valueOf(MAX_TWEET_LENGTH - charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }
}