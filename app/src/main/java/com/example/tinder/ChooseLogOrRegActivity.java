package com.example.tinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseLogOrRegActivity extends AppCompatActivity {

    private Button mLogin, mRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_log_or_reg);

        mLogin = findViewById(R.id.login);
        mRegistration = findViewById(R.id.registration);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLogOrRegActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLogOrRegActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}