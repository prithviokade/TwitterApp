package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with the view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // Define a ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfPic);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvRelDate = itemView.findViewById(R.id.tvRelDate);
            ivUrl = itemView.findViewById(R.id.ivUrl);
            tvRetweets = itemView.findViewById(R.id.tvRetweets);
            tvFavs = itemView.findViewById(R.id.tvFavs);
            btnFav = itemView.findViewById(R.id.btnFav);
            btnRetweet = itemView.findViewById(R.id.btnRetweet);
            btnReply = itemView.findViewById(R.id.btnReply);
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvRelDate.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            tvRetweets.setText(Long.toString(tweet.retweets));
            tvFavs.setText(Long.toString(tweet.favorites));
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

            Glide.with(context).load(tweet.user.imageURL).transform(new CircleCrop()).into(ivProfile);
            if (!(tweet.entityUrl.isEmpty())) {
                ivUrl.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.entityUrl).into(ivUrl);
            } else {
                ivUrl.setVisibility(View.GONE);
            }
            client = TwitterApplication.getRestClient(context);

            btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ReplyActivity.class);
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet)); // pass data
                    context.startActivity(intent); // show activity
                }
            });


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
                                tvRetweets.setTextColor(Color.argb(255, 0, 211, 30));
                                tweet.isRetweeted = true;
                                tweet.retweets++;
                                tvRetweets.setText(Long.toString(tweet.retweets));
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
                                tvRetweets.setTextColor(Color.argb(255, 170, 184, 194));
                                tweet.isRetweeted = false;
                                tweet.retweets--;
                                tvRetweets.setText(Long.toString(tweet.retweets));
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
                                tvFavs.setTextColor(Color.argb(255, 211, 184, 194));
                                tweet.isFavorited = true;
                                tweet.favorites++;
                                tvFavs.setText(Long.toString(tweet.favorites));
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
                                tvFavs.setTextColor(Color.argb(255, 170, 184, 194));
                                tweet.isFavorited = false;
                                tweet.favorites--;
                                tvFavs.setText(Long.toString(tweet.favorites));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("TweetsAdapter", "onFailure to unfavorite" + response, throwable);
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            Log.d("TWEETSADAPTERHERE", "HIIIIIII");
            int position = getAdapterPosition();
            Log.d("TWEETSADAPTERHERE", Integer.toString(position));
            if (position != RecyclerView.NO_POSITION) { // check validity of position
                Tweet tweet = tweets.get(position);
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet)); // pass data
                context.startActivity(intent); // show activity

            }
        }
    }

}
