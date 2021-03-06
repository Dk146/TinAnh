package com.example.firebasetesting.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.firebasetesting.R;
import com.example.firebasetesting.matches.MatchesActivity;
import com.example.firebasetesting.whoLikeYou.WhoLikeYouActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageActivity extends AppCompatActivity {
    public ImageView [] indicator;
    private static final int TEXT_REQUEST = 1;
    ViewPager2 viewPager2;
    FragmentStateAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        viewPager2 = findViewById(R.id.viewPaper2);
        adapter = new ScreenSlideBaseAdapter(this);

        viewPager2.setAdapter(adapter);
        indicator = new ImageView[4];
        indicator[0] = findViewById(R.id.indicator0);
        indicator[1] = findViewById(R.id.indicator1);
        indicator[2] = findViewById(R.id.indicator2);
        indicator[3] = findViewById(R.id.indicator3);

        for (int k = 1; k < 4; k++){
            indicator[k].setVisibility(View.INVISIBLE);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        viewPager2.setCurrentItem(0, true);
                        setIndicator(0);
                        return true;
                    case R.id.matches:
                        viewPager2.setCurrentItem(1, true);
                        setIndicator(1);
                        return true;
                    case R.id.liked:
                        viewPager2.setCurrentItem(2, true);
                        setIndicator(2);
                        return true;
                    case R.id.setting:
                        viewPager2.setCurrentItem(3, true);
                        setIndicator(3);
                        return true;
                }
                return false;
            }
        });
    }

    private void setIndicator(int i) {
        for (int k = 0; k < 4; k++){
            if(k==i){
                indicator[k].setVisibility(View.VISIBLE);
            }
            else {
                indicator[k].setVisibility(View.INVISIBLE);
            }
        }
    }

    private class ScreenSlideBaseAdapter extends FragmentStateAdapter{
        public MainFragment mainFragment = new MainFragment();
        public MatchesFragment matchesFragment = new MatchesFragment();
        public WhoLikeYouFragment whoLikeYouFragment = new WhoLikeYouFragment();
        public SettingFragment settingFragment = new SettingFragment();
        public ScreenSlideBaseAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return mainFragment;
            } else if (position == 1){
                return matchesFragment;
            } else if (position == 2){
                return whoLikeYouFragment;
            } else {
                return settingFragment;
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentStateAdapter adapter1 = (FragmentStateAdapter) adapter;
        WhoLikeYouFragment whoFragment = (WhoLikeYouFragment) adapter1.createFragment(2);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply = data.getStringExtra(OtherProfileActivity.EXTRA_REPLY);
                for (int i = 0; i < whoFragment.likedList.size(); ++i) {
                    if (whoFragment.likedList.get(i).ID.equals(reply)) {
                        whoFragment.likedList.remove(i);
                        whoFragment.mLikedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}