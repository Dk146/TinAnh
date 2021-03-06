package com.example.firebasetesting.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.DAOUser;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.example.firebasetesting.activity.ViewOnlyActivity;
import com.example.firebasetesting.matches.MatchAdapter;
import com.example.firebasetesting.matches.MatchesObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private LinkedList<ChatObject> mChatList = new LinkedList<ChatObject>();
    private String mCurrentUserID, matchID, chatID;
    private EditText mSendEditText;
    private ImageView mAvatar;
    private TextView mName;
    String textName;
    private ImageView mSendBtn;
    private DatabaseReference mDBUser, mDBChat;
    LinearLayoutManager linearLayoutManager;
    LinearLayout mInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchID = getIntent().getExtras().getString("matchID");
        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDBUser = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mCurrentUserID).child("Connection").child("Matches").child(matchID).child("ChatID");
        mDBChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatID();
        getUserMatchView();

        mSendBtn = findViewById(R.id.send);
        mSendEditText = findViewById(R.id.message);
        mInfoView = findViewById(R.id.infoView);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mChatAdapter = new ChatAdapter(this, mChatList);
        mRecyclerView.setAdapter(mChatAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ViewOnlyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("otherID", matchID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void getUserMatchView() {
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        DatabaseReference matchDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(matchID);
        matchDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    Log.d("Username", snapshot.getKey());
                    if (map.get("Name") != null) {
                        textName = map.get("Name").toString();
                        mName.setText(textName);
                    }
                    if (map.get("ProfileImageUrl") != null){
                        String profileImageUrl = map.get("ProfileImageUrl").toString();
                        switch (profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.tinder_app).into(mAvatar);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mAvatar);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if (!sendMessageText.isEmpty()){
            DatabaseReference newMessageDB = mDBChat.push();

            DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("UserInfo");
            userDB.child(mCurrentUserID).child("Connection").child("Matches").child(matchID).child("Status").setValue(true);
            userDB.child(matchID).child("Connection").child("Matches").child(mCurrentUserID).child("Status").setValue(true);
            userDB.child(matchID).child("Connection").child("Matches").child(mCurrentUserID).child("LastMessage").setValue(sendMessageText);
            userDB.child(mCurrentUserID).child("Connection").child("Matches").child(matchID).child("LastMessage").setValue(sendMessageText);


            Map newMessage = new HashMap();
            newMessage.put("CreateByUser", mCurrentUserID);
            newMessage.put("Text", sendMessageText);

            newMessageDB.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void getChatID() {
        mDBUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    chatID = snapshot.getValue().toString();
                    mDBChat = mDBChat.child(chatID);
                    getChatMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatMessage() {
        mDBChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String message = null;
                    String createdByUser = null;

                    if (snapshot.child("Text").getValue() != null) {
                        message = snapshot.child("Text").getValue().toString();
                    }
                    if (snapshot.child("CreateByUser").getValue() != null) {
                        createdByUser = snapshot.child("CreateByUser").getValue().toString();
                    }

                    if (message != null && createdByUser != null) {
                        Boolean currentUserBoolean = false;
                        if (createdByUser.equals(mCurrentUserID)) {
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        mChatList.add(newMessage);
                        linearLayoutManager.setStackFromEnd(true);
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}