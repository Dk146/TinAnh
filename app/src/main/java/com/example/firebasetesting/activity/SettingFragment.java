package com.example.firebasetesting.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    private static final int RESULT_OK = -1;
    private EditText mName, mPhoneNumber, mDescription, mJobTitle;
    private Button btn_back, btn_confirm, btn_logout;
    private ImageView mAvatar;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDB;
    private String userID, name, phoneNumber, profileImageUrl, userSex, description, jobTitle;
    private Uri resultUri;

    public SettingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.activity_setting, container, false);

        mName = view.findViewById(R.id.name);
        mPhoneNumber = view.findViewById(R.id.phone);
        mAvatar = view.findViewById(R.id.avatar);
        mDescription = view.findViewById(R.id.description);
        mJobTitle = view.findViewById(R.id.jobTitle);
        btn_logout = view.findViewById(R.id.logOut);

        btn_back = view.findViewById(R.id.back_button);
        btn_confirm = view.findViewById(R.id.confirm_button);

        mAvatar.setImageResource(R.drawable.tinder_app);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mUserDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userID);

        displayUserInfo();

        mAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        btn_confirm.setOnClickListener(v -> saveUserInformation());
        btn_logout.setOnClickListener(v -> logoutUser(view));

        return view;
    }

    private void displayUserInfo() {
        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("Name") != null){
                        name = map.get("Name").toString();
                        mName.setText(name);
                    }
                    if (map.get("Phone") != null){
                        phoneNumber = map.get("Phone").toString();
                        mPhoneNumber.setText(phoneNumber);
                    }
                    if (map.get("Description") != null){
                        description = map.get("Description").toString();
                        mDescription.setText(description);
                    }
                    if (map.get("JobTitle") != null){
                        jobTitle = map.get("JobTitle").toString();
                        mJobTitle.setText(jobTitle);
                    }
                    if (map.get("Sex") != null){
                        userSex = map.get("Sex").toString();
                    }
                    if (map.get("ProfileImageUrl") != null){
                        profileImageUrl = map.get("ProfileImageUrl").toString();
                        switch (profileImageUrl){
                            case "default":
                                Glide.with(getActivity().getApplication()).load(R.drawable.tinder_app).into(mAvatar);
                                break;
                            default:
                                Glide.with(getActivity().getApplication()).load(profileImageUrl).into(mAvatar);
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

    private void saveUserInformation() {
        name = mName.getText().toString();
        phoneNumber = mPhoneNumber.getText().toString();
        description = mDescription.getText().toString();
        jobTitle = mJobTitle.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("Name", name);
        userInfo.put("Phone", phoneNumber);
        userInfo.put("Description", description);
        userInfo.put("JobTitle", jobTitle);

        mUserDB.updateChildren(userInfo);
        if (resultUri != null) {
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(userID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    getActivity().finish();
                }
            });

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        Map userInfo = new HashMap();
                        userInfo.put("ProfileImageUrl", downloadUrl.toString());
                        mUserDB.updateChildren(userInfo);
                        getActivity().finish();
                        return;
                    } else {

                    }
                }
            });

        }else{
            getActivity().finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mAvatar.setImageURI(resultUri);
        }
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(view.getContext(), LoginRegisterActivity.class);
        startActivity(intent);
        getActivity().finish();
        return;
    }
}