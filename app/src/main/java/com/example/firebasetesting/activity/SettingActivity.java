package com.example.firebasetesting.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebasetesting.R;
import com.example.firebasetesting.matches.MatchesActivity;
import com.example.firebasetesting.whoLikeYou.WhoLikeYouActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class SettingActivity extends AppCompatActivity {
    private EditText mName, mPhoneNumber, mDescription, mJobTitle;
    private Button btn_back, btn_confirm;
    private ImageView mAvatar;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDB;
    private String userID, name, phoneNumber, profileImageUrl, userSex, description, jobTitle;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mName = findViewById(R.id.name);
        mPhoneNumber = findViewById(R.id.phone);
        mAvatar = findViewById(R.id.avatar);
        mDescription = findViewById(R.id.description);
        mJobTitle = findViewById(R.id.jobTitle);

        btn_back = findViewById(R.id.back_button);
        btn_confirm = findViewById(R.id.confirm_button);

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

//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
//        bottomNavigationView.setSelectedItemId(R.id.setting);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.home:
//                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.matches:
//                        startActivity(new Intent(SettingActivity.this, MatchesActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.liked:
//                        startActivity(new Intent(SettingActivity.this, WhoLikeYouActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.setting:
//                        return true;
//                }
//                return false;
//            }
//        });
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
                                Glide.with(getApplication()).load(R.drawable.tinder_app).into(mAvatar);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mAvatar);
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
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
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
                    finish();
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
                        userInfo.put("ProfileImageUrl", downloadUrl);
                        mUserDB.updateChildren(userInfo);
                        finish();
                        return;
                    } else {

                    }
                }
            });

        }else{
            //finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mAvatar.setImageURI(resultUri);
        }
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(SettingActivity.this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}