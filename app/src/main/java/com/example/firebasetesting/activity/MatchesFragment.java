package com.example.firebasetesting.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebasetesting.DAOUser;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.example.firebasetesting.chat.ChatObject;
import com.example.firebasetesting.matches.BubbleMatchAdapter;
import com.example.firebasetesting.matches.MatchAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewHorizontal;
    private MatchAdapter matchAdapter;
    private BubbleMatchAdapter mBubbleMatchAdapter;
    private LinkedList<UserInfo> mMatchList = new LinkedList<>();
    private LinkedList<UserInfo> mHasMessageList = new LinkedList<>();
    private LinkedList<String> mListLastMessage = new LinkedList<>();
    private String mCurrentUserID, chatID;
    private DatabaseReference mDBChat;


    public MatchesFragment() {
        // Required empty public constructor
    }


    public static MatchesFragment newInstance(String param1, String param2) {
        MatchesFragment fragment = new MatchesFragment();
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
        View view = inflater.inflate(R.layout.activity_matches, container, false);
        bindView(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void bindView(View view) {
        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mDBChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        matchAdapter = new MatchAdapter(view.getContext(), mHasMessageList, mListLastMessage);
        mBubbleMatchAdapter = new BubbleMatchAdapter(view.getContext(), mMatchList);

        DAOUser daoUser = new DAOUser();
        daoUser.getUsersMatchMessageID(mCurrentUserID, mHasMessageList, matchAdapter, mListLastMessage);
        daoUser.getUsersMatchID(mCurrentUserID, mMatchList, mBubbleMatchAdapter);

        //matchAdapter = new MatchAdapter(this, mMatchList);
        mRecyclerView.setAdapter(matchAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mRecyclerViewHorizontal = (RecyclerView) view.findViewById(R.id.horizontal_only);
        mRecyclerViewHorizontal.setNestedScrollingEnabled(false);
        mRecyclerViewHorizontal.setHasFixedSize(true);


        //mBubbleMatchAdapter = new BubbleMatchAdapter(this, mMatchList);
        mRecyclerViewHorizontal.setAdapter(mBubbleMatchAdapter);
        mRecyclerViewHorizontal.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
    }


    @Override
    public void onResume() {
        DAOUser daoUser = new DAOUser();
        Log.d("LastMessage", "onResume");
        mHasMessageList.clear();
        mListLastMessage.clear();
        daoUser.getUsersMatchMessageID(mCurrentUserID, mHasMessageList, matchAdapter, mListLastMessage);
        mRecyclerView.setAdapter(matchAdapter);
        super.onResume();
    }
}