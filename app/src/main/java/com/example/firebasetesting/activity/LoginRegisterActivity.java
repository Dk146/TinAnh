package com.example.firebasetesting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasetesting.R;

public class LoginRegisterActivity extends AppCompatActivity {
    public TextView login;
    public Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        login = (TextView) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginRegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginRegisterActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }
}