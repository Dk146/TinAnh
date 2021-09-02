package com.example.firebasetesting.matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.firebasetesting.DAOUser;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.example.firebasetesting.activity.MainActivity;
import com.example.firebasetesting.activity.SettingActivity;
import com.example.firebasetesting.whoLikeYou.WhoLikeYouActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MatchAdapter matchAdapter;
    private LinkedList<UserInfo> mMatchList = new LinkedList<>();
    private String mCurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        matchAdapter = new MatchAdapter(this, mMatchList);
        mRecyclerView.setAdapter(matchAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DAOUser daoUser = new DAOUser();
        daoUser.getUsersMatchID(mCurrentUserID, mMatchList, matchAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.matches);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(MatchesActivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.matches:
                        return true;
                    case R.id.liked:
                        startActivity(new Intent(MatchesActivity.this, WhoLikeYouActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.setting:
                        startActivity(new Intent(MatchesActivity.this, SettingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
}