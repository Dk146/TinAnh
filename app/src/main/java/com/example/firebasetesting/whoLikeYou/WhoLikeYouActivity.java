package com.example.firebasetesting.whoLikeYou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.firebasetesting.R;
import com.example.firebasetesting.activity.MainActivity;
import com.example.firebasetesting.activity.SettingActivity;
import com.example.firebasetesting.matches.MatchesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WhoLikeYouActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_like_you);

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