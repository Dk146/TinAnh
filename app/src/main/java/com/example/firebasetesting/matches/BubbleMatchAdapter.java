package com.example.firebasetesting.matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.example.firebasetesting.chat.ChatActivity;

import java.util.LinkedList;

public class BubbleMatchAdapter extends RecyclerView.Adapter<BubbleMatchAdapter.BubbleMatchViewHolder> {
    LinkedList<UserInfo> mMatchList = new LinkedList<>();
    Context mContext;
    LayoutInflater mInflater;

    public BubbleMatchAdapter(Context context, LinkedList<UserInfo> matchList){
        mInflater = LayoutInflater.from(context);
        mMatchList = matchList;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public BubbleMatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mMatchView = mInflater.inflate(R.layout.item_buble, parent, false);
        return new BubbleMatchViewHolder(mMatchView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BubbleMatchViewHolder holder, int position) {
        UserInfo mCurrentUser = mMatchList.get(position);
        holder.mMatchID = mCurrentUser.getID();
        if (!mCurrentUser.getProfileImageUrl().equals("default")){
            Glide.with(mContext).load(mCurrentUser.getProfileImageUrl()).into(holder.mImg);
        }
    }

    @Override
    public int getItemCount() {
        return mMatchList.size();
    }

    public class BubbleMatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImg;
        String mMatchID;
        final BubbleMatchAdapter adapter;

        public BubbleMatchViewHolder(@NonNull View itemView, BubbleMatchAdapter bubbleMatchAdapter) {
            super(itemView);
            mImg = itemView.findViewById(R.id.bubble_img);
            this.adapter = bubbleMatchAdapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            Bundle b = new Bundle();
            b.putString("matchID", mMatchID);
            intent.putExtras(b);
            view.getContext().startActivity(intent);
        }
    }
}
