package com.example.firebasetesting;

import android.util.Log;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    public void getLikedUserID(String mCurrentUserID, LinkedList<UserInfo> likedList,RecyclerView.Adapter mLikedAdapter) {
        DatabaseReference likedDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mCurrentUserID).child("Connection").child("Yeps");
        likedDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot liked : snapshot.getChildren()) {
                        fetchInfo(liked.getKey(), likedList, mLikedAdapter);
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
                mLikedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
