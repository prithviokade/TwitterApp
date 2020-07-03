package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeFragment extends DialogFragment {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeActivity";

    private EditText etCompose;
    private Button btnTweet;
    private TextView tvCharLeft;

    Tweet tweet;

    TwitterClient client;

    // Empty constructor is required for DialogFragment
    public ComposeFragment() {}

    public static ComposeFragment newInstance() {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }


    public interface ComposeFragmentListener {
        void onActivityResult(Tweet tweet);
    }

    ComposeFragmentListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compose, container);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = TwitterApplication.getRestClient(getContext());

        etCompose = (EditText) view.findViewById(R.id.etCompose);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        tvCharLeft = (TextView) view.findViewById(R.id.tvCharLeft);

        // Set click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                // Make API call on Twitter to post

                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published Tweet: " + tweet.body);
                            listener = (ComposeFragmentListener) getActivity();
                            listener.onActivityResult(tweet);
                            dismiss();
                            // closes dialog, passes data to parent
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet" + response, throwable);
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
                if (charSequence.length() > 0 && charSequence.length() <= MAX_TWEET_LENGTH) {
                    btnTweet.setEnabled(true);
                    btnTweet.setBackgroundColor(Color.argb(255, 29, 161, 242));
                } else {
                    btnTweet.setEnabled(false);
                    btnTweet.setBackgroundColor(Color.argb(255, 170, 184, 194));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

    }
}