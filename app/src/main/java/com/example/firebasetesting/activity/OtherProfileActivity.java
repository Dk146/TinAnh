package com.example.firebasetesting.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.DAOUser;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class OtherProfileActivity extends AppCompatActivity {
    ImageView mAvatar;
    TextView mName, mJobTitle, mDescription;
    String name, description, jobTitle, profileImageUrl;
    private DatabaseReference mUserDB, mOtherUserDB;
    FloatingActionButton mLike, mDislike;
    String otherID;
    String userID;
    public static final String EXTRA_REPLY =
            "com.example.android.twoactivities.extra.REPLY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mJobTitle = findViewById(R.id.jobTitle);
        mDescription = findViewById(R.id.description);

        mLike = findViewById(R.id.like);
        mDislike = findViewById(R.id.dislike);

        otherID = getIntent().getExtras().get("otherID").toString();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DAOUser daoUser = new DAOUser();
        UserInfo otherProfile;
        daoUser.getUserInfo(otherID);
        otherProfile = daoUser.userInfo;

        mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(otherID);
        displayOther();
        mUserDB = FirebaseDatabase.getInstance().getReference().child("UserInfo");
    }

    private void displayOther() {
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("Name") != null){
                        name = map.get("Name").toString();
                        mName.setText(name);
                    }
                    if (map.get("Description") != null && !map.get("Description").equals("")){
                        description = map.get("Description").toString();
                        mDescription.setText(description);
                    } else {
                        mDescription.setVisibility(View.GONE);
                    }
                    if (map.get("JobTitle") != null && !map.get("JobTitle").equals("")){
                        jobTitle = map.get("JobTitle").toString();
                        mJobTitle.setText(jobTitle);
                    } else {
                        mJobTitle.setVisibility(View.GONE);
                    }
                    if (map.get("ProfileImageUrl") != null){
                        profileImageUrl = map.get("ProfileImageUrl").toString();
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

    public void swipeLeft(View view) {
        mUserDB.child(otherID).child("Connection").child("Nope").child(userID).setValue(true);

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, otherID);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void swipeRight(View view) {
        mUserDB.child(otherID).child("Connection").child("Yeps").child(userID).setValue(true);
        isConnectionMatch();

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, otherID);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    private void isConnectionMatch() {
        Log.d("Matches", otherID);
        Log.d("Matches", userID);

        DatabaseReference currentUserConnectionDB = mUserDB.child(userID).child("Connection").child("Yeps").child(otherID);
        currentUserConnectionDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(OtherProfileActivity.this, "It's a match", Toast.LENGTH_SHORT).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    mUserDB.child(snapshot.getKey()).child("Connection").child("Matches").child(userID).child("ChatID").setValue(key);
                    mUserDB.child(userID).child("Connection").child("Matches").child(snapshot.getKey()).child("ChatID").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}