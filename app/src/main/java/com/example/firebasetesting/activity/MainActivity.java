package com.example.firebasetesting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasetesting.ItemArrayAdapter;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.example.firebasetesting.matches.MatchesActivity;
import com.example.firebasetesting.whoLikeYou.WhoLikeYouActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ItemArrayAdapter arrayAdapter;
    private UserInfo userInfo[];

    SwipeFlingAdapterView flingAdapterView;
    private Button like, dislike;

    private FirebaseAuth mAuth;
    private DatabaseReference userDB;
    private String currentUId;

    private String userSex;
    private String oppositeUserSex;

    ListView listView;
    List<UserInfo> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Oncreate","hehe");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        userDB = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        checkUserSex();

        like = (Button) findViewById(R.id.like);
        dislike = (Button) findViewById(R.id.dislike);
        flingAdapterView = findViewById(R.id.swipe);

        rowItems = new ArrayList<UserInfo>();

        arrayAdapter = new ItemArrayAdapter(MainActivity.this, R.layout.item, rowItems);

        flingAdapterView.setAdapter(arrayAdapter);
        flingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object o) {
                UserInfo user = (UserInfo) o;
                String userID = user.getID();
                userDB.child(userID).child("Connection").child("Nope").child(currentUId).setValue(true);
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRightCardExit(Object o) {
                UserInfo user = (UserInfo) o;
                String userID = user.getID();
                userDB.child(userID).child("Connection").child("Yeps").child(currentUId).setValue(true);
                isConnectionMatch(userID);
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {

            }

            @Override
            public void onScroll(float v) {

            }

        });

        flingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int i, Object o) {
                Log.d("Clicked", "Clicked");
                UserInfo otherUser = (UserInfo) o;
                Intent intent = new Intent(MainActivity.this, OtherProfileActivity.class);
                Bundle b = new Bundle();
                b.putString("otherID", otherUser.ID);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingAdapterView.getTopCardListener().selectRight();
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingAdapterView.getTopCardListener().selectLeft();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.matches:
                        startActivity(new Intent(MainActivity.this, MatchesActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.liked:
                        startActivity(new Intent(MainActivity.this, WhoLikeYouActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void isConnectionMatch(String userID) {
        DatabaseReference currentUserConnectionDB = userDB.child(currentUId).child("Connection").child("Yeps").child(userID);
        currentUserConnectionDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "It's a match", Toast.LENGTH_SHORT).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    //userDB.child(snapshot.getKey()).child("Connection").child("Matches").child(currentUId).setValue(true);
                    userDB.child(snapshot.getKey()).child("Connection").child("Matches").child(currentUId).child("ChatID").setValue(key);

                    //userDB.child(currentUId).child("Connection").child("Matches").child(snapshot.getKey()).setValue(true);
                    userDB.child(currentUId).child("Connection").child("Matches").child(snapshot.getKey()).child("ChatID").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference currentUser = userDB.child(user.getUid());
        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("Sex").getValue() != null) {
                        userSex = snapshot.child("Sex").getValue().toString();
                        switch (userSex) {
                            case "Male":
                                oppositeUserSex = "Female";
                                break;
                            default:
                                oppositeUserSex = "Male";
                                break;
                        }
                        getOppositeSexUser();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public void getOppositeSexUser() {
        userDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("Sex").getValue() != null) {
                    Log.d("Sex", oppositeUserSex);
                    if (snapshot.exists() && !snapshot.child("Connection").child("Nope").hasChild(currentUId)
                            && !snapshot.child("Connection").child("Yeps").hasChild(currentUId)
                            && snapshot.child("Sex").getValue().toString().equals(oppositeUserSex)) {
                        String profileImageUrl = "default";
                        if (!snapshot.child("ProfileImageUrl").getValue().toString().equals("default")) {
                            profileImageUrl = snapshot.child("ProfileImageUrl").getValue().toString();
                        }
                        UserInfo user = new UserInfo(snapshot.getKey(), snapshot.child("Name").getValue().toString(), userSex, profileImageUrl);
                        rowItems.add(user);
                        arrayAdapter.notifyDataSetChanged();
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "Yep");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("onRestart", "Yep");
        if (rowItems.size() != 0) {
            DatabaseReference otherUserDB = userDB.child(rowItems.get(0).ID);
            otherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("Connection").child("Yeps").hasChild(currentUId) || snapshot.child("Connection").child("Nope").hasChild(currentUId)){
                        rowItems.remove(0);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}