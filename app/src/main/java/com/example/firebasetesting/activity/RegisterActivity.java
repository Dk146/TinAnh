package com.example.firebasetesting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasetesting.DAOUser;
import com.example.firebasetesting.R;
import com.example.firebasetesting.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private Button register;
    private EditText email, password, name;
    private RadioGroup radioGroup;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        register = (Button) findViewById(R.id.register);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name = (EditText)findViewById(R.id.name);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        register.setOnClickListener(v -> {
            final String mEmail = email.getText().toString();
            final String mPassword = password.getText().toString();
            final int selectedID = radioGroup.getCheckedRadioButtonId();
            final RadioButton radioButton = (RadioButton) findViewById(selectedID);
            final String mName = name.getText().toString();

            if (radioButton.getText() == null){
                return;
            }
                mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Sign up Error", Toast.LENGTH_SHORT);
                            Log.d("Register", "No");

                        } else {
                            String userID = mAuth.getCurrentUser().getUid();
                            DAOUser dao = new DAOUser();
                            UserInfo user = new UserInfo(userID, name.getText().toString(), radioButton.getText().toString(), "default");

                            dao.add(user).addOnSuccessListener(sc->{
                                Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(fl -> {
                                Toast.makeText(RegisterActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}