package com.example.firebasetesting.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.DAOUser;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
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
    private DatabaseReference mUserDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mJobTitle = findViewById(R.id.jobTitle);
        mDescription = findViewById(R.id.description);

        String otherID = getIntent().getExtras().get("otherID").toString();
        DAOUser daoUser = new DAOUser();

        UserInfo otherProfile;
        daoUser.getUserInfo(otherID);
        otherProfile = daoUser.userInfo;

        mUserDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(otherID);
        displayOther();

    }

    private void displayOther() {
        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("Name") != null){
                        name = map.get("Name").toString();
                        mName.setText(name);
                    }
                    if (map.get("Description") != null){
                        description = map.get("Description").toString();
                        mDescription.setText(description);
                    }
                    if (map.get("JobTitle") != null){
                        jobTitle = map.get("JobTitle").toString();
                        mJobTitle.setText(jobTitle);
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
                        //Glide.with(getApplication()).load(profileImageUrl).into(mAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}