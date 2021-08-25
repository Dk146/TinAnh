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
import android.view.View;
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

public class SettingActivity extends AppCompatActivity {

    private EditText mName, mPhoneNumber;
    private Button btn_back, btn_confirm;
    private ImageView mAvatar;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDB;
    private String userID, name, phoneNumber, profileImageUrl, userSex;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mName = findViewById(R.id.name);
        mPhoneNumber = findViewById(R.id.phone);
        mAvatar = findViewById(R.id.avatar);
        btn_back = findViewById(R.id.back_button);
        btn_confirm = findViewById(R.id.confirm_button);

        mAvatar.setImageResource(R.drawable.tinder_app);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mUserDB = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userID);

        displayUserInfo();

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
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
                        Log.d("DataChange",profileImageUrl);
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

        Map userInfo = new HashMap();
        userInfo.put("Name", name);
        userInfo.put("Phone", phoneNumber);

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
                        Log.d("ImageUrl", downloadUrl.toString());
                        Map userInfo = new HashMap();
                        userInfo.put("ProfileImageUrl", downloadUrl.toString());
                        mUserDB.updateChildren(userInfo);
                        finish();
                        return;
                    } else {

                    }
                }
            });

        }else{
            finish();
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
}