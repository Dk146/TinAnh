package com.example.firebasetesting.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebasetesting.DAOUser;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.example.firebasetesting.whoLikeYou.LikedAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WhoLikeYouFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WhoLikeYouFragment extends Fragment {

    private static final int RESULT_OK = -1;
    String mCurrentUserID;
    public LinkedList<UserInfo> likedList = new LinkedList<>();
    RecyclerView mRecyclerView;
    public LikedAdapter mLikedAdapter;
    public static final String EXTRA_MESSAGE
            = "com.example.android.twoactivities.extra.MESSAGE";
    // Unique tag for the intent reply
    public static final int TEXT_REQUEST = 1;

    public WhoLikeYouFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static WhoLikeYouFragment newInstance(String param1, String param2) {
        WhoLikeYouFragment fragment = new WhoLikeYouFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_who_like_you, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //getLikedUserID();


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mLikedAdapter = new LikedAdapter(view.getContext(), likedList);
        mRecyclerView.setAdapter(mLikedAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

        DAOUser daoUser = new DAOUser();
        daoUser.getLikedUserID(mCurrentUserID, likedList, mLikedAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Test for the right intent reply.
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply = data.getStringExtra(OtherProfileActivity.EXTRA_REPLY);
                for (int i = 0; i < likedList.size(); ++i) {
                    if (likedList.get(i).ID.equals(reply)) {
//                        likedList.remove(i);
//                        mLikedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DAOUser daoUser = new DAOUser();
        likedList.clear();
        daoUser.getLikedUserID(mCurrentUserID, likedList, mLikedAdapter);
        mLikedAdapter.notifyDataSetChanged();
    }

}