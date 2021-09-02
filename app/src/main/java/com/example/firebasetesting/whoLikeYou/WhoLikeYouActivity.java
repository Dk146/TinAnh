package com.example.firebasetesting.whoLikeYou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.firebasetesting.DAOUser;
import com.example.firebasetesting.ItemArrayAdapter;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.example.firebasetesting.activity.MainActivity;
import com.example.firebasetesting.activity.SettingActivity;
import com.example.firebasetesting.matches.MatchesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.LinkedList;

public class WhoLikeYouActivity extends AppCompatActivity {
    String mCurrentUserID;
    LinkedList<UserInfo> likedList = new LinkedList<>();
    RecyclerView mRecyclerView;
    LikedAdapter mLikedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_like_you);

        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //getLikedUserID();


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mLikedAdapter = new LikedAdapter(this, likedList);
        mRecyclerView.setAdapter(mLikedAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        DAOUser daoUser = new DAOUser();
        daoUser.getLikedUserID(mCurrentUserID, likedList, mLikedAdapter);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.liked);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(WhoLikeYouActivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.matches:
                        startActivity(new Intent(WhoLikeYouActivity.this, MatchesActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.liked:
                        return true;
                    case R.id.setting:
                        startActivity(new Intent(WhoLikeYouActivity.this, SettingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
}