package com.example.firebasetesting.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firebasetesting.ItemArrayAdapter;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    public ItemArrayAdapter arrayAdapter;
    private UserInfo userInfo[];

    SwipeFlingAdapterView flingAdapterView;
    private ImageView like, dislike;

    private FirebaseAuth mAuth;
    private DatabaseReference userDB;
    private String currentUId;

    private String userSex;
    private String oppositeUserSex;

    ListView listView;
    List<UserInfo> rowItems;

    public MainFragment() {

    }


    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        userDB = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        checkUserSex();

        like = (ImageView) view.findViewById(R.id.like);
        dislike = (ImageView) view.findViewById(R.id.dislike);
        flingAdapterView = view.findViewById(R.id.swipe);

        rowItems = new ArrayList<UserInfo>();
        arrayAdapter = new ItemArrayAdapter(view.getContext(), R.layout.item, rowItems);

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
                Intent intent = new Intent(view.getContext(), OtherProfileActivity.class);
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


        return view;
    }

    private void isConnectionMatch(String userID) {
        DatabaseReference currentUserConnectionDB = userDB.child(currentUId).child("Connection").child("Yeps").child(userID);
        currentUserConnectionDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    //userDB.child(snapshot.getKey()).child("Connection").child("Matches").child(currentUId).setValue(true);
                    userDB.child(snapshot.getKey()).child("Connection").child("Matches").child(currentUId).child("ChatID").setValue(key);
                    //userDB.child(currentUId).child("Connection").child("Matches").child(snapshot.getKey()).setValue(true);
                    userDB.child(currentUId).child("Connection").child("Matches").child(snapshot.getKey()).child("ChatID").setValue(key);
                    userDB.child(currentUId).child("Connection").child("Matches").child(snapshot.getKey()).child("Status").setValue(false);
                    userDB.child(snapshot.getKey()).child("Connection").child("Matches").child(currentUId).child("Status").setValue(false);
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
    public void onResume() {
        super.onResume();
//        Log.d("OnresumeFragment", "OnresumeFragment");
//        if (rowItems.size() != 0) {
//            DatabaseReference otherUserDB = userDB.child(rowItems.get(0).ID);
//            otherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.child("Connection").child("Yeps").hasChild(currentUId) || snapshot.child("Connection").child("Nope").hasChild(currentUId)){
//                        rowItems.remove(0);
//                        arrayAdapter.notifyDataSetChanged();
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                }
//            });
//
//        }
        rowItems.clear();
        checkUserSex();
        arrayAdapter.notifyDataSetChanged();
    }
}