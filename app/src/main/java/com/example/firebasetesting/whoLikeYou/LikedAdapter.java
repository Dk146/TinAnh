package com.example.firebasetesting.whoLikeYou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;

import java.util.LinkedList;

public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.LikedViewHolder> {
    private LinkedList<UserInfo> likedList = new LinkedList<>();
    private LayoutInflater mInflater;
    private Context context;

    public LikedAdapter(Context context, LinkedList<UserInfo> likedList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.likedList = likedList;

    }

    public class LikedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView avatar;
        TextView name;
        final LikedAdapter mLikeAdapter;
        public LikedViewHolder(@NonNull View itemView, LikedAdapter adapter) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            mLikeAdapter = adapter;
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public LikedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemArrayAdapter = mInflater.inflate(R.layout.item_liked, parent, false);
        return new LikedViewHolder(mItemArrayAdapter, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedViewHolder holder, int position) {
        UserInfo mCurrentUser = likedList.get(position);
        holder.name.setText(mCurrentUser.name);
        if (!mCurrentUser.getProfileImageUrl().equals("default")){
            Glide.with(context).load(mCurrentUser.getProfileImageUrl()).into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return likedList.size();
    }

}
