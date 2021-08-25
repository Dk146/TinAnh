package com.example.firebasetesting;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DAOUser {
    private DatabaseReference databaseReference;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(UserInfo.class.getSimpleName());
    }

    public Task<Void> add(UserInfo userInfo){
        Map Info = new HashMap<>();
        Info.put("Name", userInfo.name);
        Info.put("Sex", userInfo.sex);
        Info.put("ProfileImageUrl", userInfo.profileImageUrl);
        return databaseReference.child(userInfo.ID).updateChildren(Info);
    }
}
