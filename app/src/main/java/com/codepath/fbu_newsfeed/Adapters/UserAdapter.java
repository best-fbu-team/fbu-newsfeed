package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";
    public static ArrayList<ParseUser> users;
    public static Context context;

    public UserAdapter(ArrayList<ParseUser> users) { this.users = users; }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View shareView = inflater.inflate(R.layout.user_item, parent, false);
        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(shareView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        final ParseUser user = users.get(position);
        holder.tvFullName.setText(user.getString(User.KEY_FULLNAME));
        holder.tvUsername.setText(user.getUsername());

        // TODO: load profile image

        // TODO: set click listeners

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addAll(List<ParseUser> list) {
        users.addAll(list);
        notifyDataSetChanged();

    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvFullName) TextView tvFullName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
