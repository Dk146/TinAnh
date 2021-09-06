package com.example.firebasetesting;

import android.util.Log;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasetesting.matches.MatchAdapter;
import com.example.firebasetesting.whoLikeYou.LikedAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DAOUser {
    private DatabaseReference databaseReference;
    public UserInfo userInfo;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(UserInfo.class.getSimpleName());
    }

    public Task<Void> add(UserInfo userInfo){
        Map Info = new HashMap<>();
        Info.put("Name", userInfo.name);
        Info.put("Sex", userInfo.sex);
        Info.put("ProfileImageUrl", userInfo.profileImageUrl);
        return databaseReference.child(userInfo.ID).updateChildren(Info);
    }

    public void getUsersMatchMessageID(String mCurrentUserID, LinkedList<UserInfo> likedList, RecyclerView.Adapter mLikedAdapter, LinkedList<String> listLastMessage) {
        DatabaseReference matchesDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mCurrentUserID).child("Connection").child("Matches");
        matchesDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot match : snapshot.getChildren()){
                        if (snapshot.child(match.getKey()).hasChild("Status")) {
                            Log.d("STATUS", match.getKey());
                            String status = snapshot.child(match.getKey()).child("Status").getValue().toString();
                            if (status.equals("true")) {
                                fetchInfo(match.getKey(), likedList, mLikedAdapter);
                                listLastMessage.add(snapshot.child(match.getKey()).child("LastMessage").getValue().toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUsersMatchID(String mCurrentUserID, LinkedList<UserInfo> likedList,RecyclerView.Adapter mLikedAdapter) {
        DatabaseReference matchesDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mCurrentUserID).child("Connection").child("Matches");
        matchesDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot match : snapshot.getChildren()){
                        fetchInfo(match.getKey(), likedList, mLikedAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getLikedUserID(String mCurrentUserID, LinkedList<UserInfo> likedList,RecyclerView.Adapter mLikedAdapter) {
        DatabaseReference likedDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mCurrentUserID).child("Connection");
        likedDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot liked : snapshot.child("Yeps").getChildren()) {
                        Log.d("Yeps", "user");
                        if (!snapshot.child("Matches").hasChild(liked.getKey())) {
                            fetchInfo(liked.getKey(), likedList, mLikedAdapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetchInfo(String liked, LinkedList<UserInfo> likedList, RecyclerView.Adapter mLikedAdapter) {

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(liked);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userID = snapshot.getKey();
                String name = "", sex = "", profileImageUrl = "default";
                if (snapshot.child("Name") != null) {
                    name = snapshot.child("Name").getValue().toString();
                    Log.d("LikedInfo", "NDK");
                }
                if (snapshot.child("Sex") != null) {
                    sex = snapshot.child("Sex").getValue().toString();
                }
                if (snapshot.child("ProfileImageUrl") != null) {
                    profileImageUrl = snapshot.child("ProfileImageUrl").getValue().toString();
                }

                UserInfo userInfo = new UserInfo(userID, name, sex, profileImageUrl);
                likedList.add(userInfo);
                Log.d("MATCHES", name);
                mLikedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserInfo(String userID) {
        Log.d("Name", userID);
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userID);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userID = snapshot.getKey();
                Log.d("Name", userID);
                String name = "", sex = "", profileImageUrl = "default", jobTitle = "", description = "";
                if (snapshot.child("Name") != null) {
                    Log.d("Name", "a");
                    name = snapshot.child("Name").getValue().toString();
                    Log.d("LikedInfo", "NDK");
                }
                if (snapshot.child("Sex") != null) {
                    sex = snapshot.child("Sex").getValue().toString();
                }
                if (snapshot.child("ProfileImageUrl") != null) {
                    profileImageUrl = snapshot.child("ProfileImageUrl").getValue().toString();
                }
                if (snapshot.hasChild("JobTitle")) {
                    jobTitle = snapshot.child("JobTitle").getValue().toString();
                }
                if (snapshot.hasChild("Description")) {
                    description = snapshot.child("Description").getValue().toString();
                }
                Log.d("Name", name);
                userInfo = new UserInfo(userID, name, sex, profileImageUrl);
                userInfo.jobTitle = jobTitle;
                Log.d("Name", userInfo.name);
                Log.d("Name", userInfo.jobTitle);
                userInfo.description = description;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
