package com.example.firebasetesting.whoLikeYou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.firebasetesting.activity.OtherProfileActivity;

import java.util.LinkedList;

public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.LikedViewHolder> {
    private LinkedList<UserInfo> likedList = new LinkedList<>();
    private LayoutInflater mInflater;
    private Context context;
    public static final int TEXT_REQUEST = 1;


    public LikedAdapter(Context context, LinkedList<UserInfo> likedList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.likedList = likedList;

    }

    public class LikedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView avatar;
        TextView name;
        String otherID;
        final LikedAdapter mLikeAdapter;
        public LikedViewHolder(@NonNull View itemView, LikedAdapter adapter) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            mLikeAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), OtherProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("otherID", otherID);
            intent.putExtras(bundle);
            ((Activity) v.getContext()).startActivityForResult(intent, TEXT_REQUEST);
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
        holder.otherID = likedList.get(position).ID;
        if (!mCurrentUser.getProfileImageUrl().equals("default")){
            Glide.with(context).load(mCurrentUser.getProfileImageUrl()).into(holder.avatar);
        }else {
            holder.avatar.setImageResource(R.drawable.tinder_app);
        }
    }

    @Override
    public int getItemCount() {
        return likedList.size();
    }

}
