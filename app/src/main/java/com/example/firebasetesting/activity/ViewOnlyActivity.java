package com.example.firebasetesting.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ViewOnlyActivity extends AppCompatActivity {
    ImageView mAvatar;
    TextView mName, mJobTitle, mDescription;
    String name, description, jobTitle, profileImageUrl;
    private DatabaseReference mUserDB, mOtherUserDB;
    String otherID;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_only);

        findViewData();

        otherID = getIntent().getExtras().get("otherID").toString();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(otherID);
        mUserDB = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        displayOther();
    }

    private void findViewData() {
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mJobTitle = findViewById(R.id.jobTitle);
        mDescription = findViewById(R.id.description);
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

}