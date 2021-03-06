package com.example.firebasetesting.matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.ItemArrayAdapter;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.example.firebasetesting.activity.MainActivity;
import com.example.firebasetesting.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.LinkedList;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private LayoutInflater mInflater;
    private final LinkedList<UserInfo> mMatch_list;
    private final LinkedList<String> mLastMessage;
    private Context context;

    public MatchAdapter(Context context, LinkedList<UserInfo> mMatch_list, LinkedList<String> mLastMessage) {
        mInflater = LayoutInflater.from(context);
        this.mMatch_list = mMatch_list;
        this.mLastMessage = mLastMessage;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemArrayAdapter = mInflater.inflate(R.layout.item_matches, parent, false);
        return new MatchViewHolder(mItemArrayAdapter, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        UserInfo mCurrent = mMatch_list.get(position);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference lastMessage = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mCurrent.ID).child("Connection").child("Matches").child(userID);

        holder.mName.setText(mCurrent.name);
        holder.mLastMessage.setText(mLastMessage.get(position));
        holder.mMatchID = mCurrent.ID;
//        lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    holder.mLastMessage.setText(snapshot.child("LastMessage").getValue().toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        if (!mCurrent.getProfileImageUrl().equals("default")){
            Glide.with(context).load(mCurrent.getProfileImageUrl()).into(holder.mMatchImg);
        }
    }

    @Override
    public int getItemCount() {
        return mMatch_list.size();
    }

    public class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mMatchImg;
        String mMatchID;
        TextView mName, mLastMessage;
        final MatchAdapter mAdapter;
        public MatchViewHolder(@NonNull View itemView, MatchAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            mMatchImg = itemView.findViewById(R.id.match_img);
            mName = itemView.findViewById(R.id.match_name);
            mLastMessage = itemView.findViewById(R.id.lastMessage);
            mAdapter = adapter;
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
