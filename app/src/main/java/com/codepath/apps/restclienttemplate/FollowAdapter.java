package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    Context context;
    List<User> users;
    TwitterClient client;

    public FollowAdapter(Context context, List<User> users) {
        Log.d("ysesh","idkkkkk");
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Followadapter","uncreateviewholder");
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow, parent, false);
        return new FollowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("positionn", Integer.toString(position));
        // Get the data at position
        User user = users.get(position);
        // Bind the tweet with the view holder
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvScreenName;
        TextView tvName;

        Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfPic);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            follow = itemView.findViewById(R.id.follow);
        }

        public void bind(User user) {
            Log.d("FOLLOWERADAPTER", user.screenName);
            tvScreenName.setText(user.screenName);
            tvName.setText(user.name);
            Glide.with(context).load(user.imageURL).transform(new CircleCrop()).into(ivProfile);

            /*
            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (follow.getText().toString() == "Follow") {
                        follow.setText("Following");
                    } else {
                        follow.setText("Follow");
                    }
                }
            });

             */
        }
    }

}
