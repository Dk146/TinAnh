package com.example.firebasetesting.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasetesting.R;

import java.util.LinkedList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private LayoutInflater mInflater;
    private final LinkedList<ChatObject> mChat_list;
    private Context context;

    public ChatAdapter(Context context, LinkedList<ChatObject> mChat_list) {
        mInflater = LayoutInflater.from(context);
        this.mChat_list = mChat_list;
        this.context = context;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView mMessage;
        public LinearLayout mContainer;
        final ChatAdapter mChatAdapter;
        public ChatViewHolder(@NonNull View itemView, ChatAdapter adapter) {
            super(itemView);
            mMessage = itemView.findViewById(R.id.message);
            mContainer = itemView.findViewById(R.id.container);
            mChatAdapter = adapter;
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemArrayAdapter = mInflater.inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(mItemArrayAdapter, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.mMessage.setText(mChat_list.get(position).getMessage());
        if (mChat_list.get(position).getCurrentUser()) {
            holder.mMessage.setBackgroundResource(R.drawable.message_bubble);
        } else {
            holder.mContainer.setGravity(Gravity.LEFT);
            holder.mMessage.setBackgroundResource(R.drawable.message_bubble_partner);
        }
    }


    @Override
    public int getItemCount() {
        return mChat_list.size();
    }
}
