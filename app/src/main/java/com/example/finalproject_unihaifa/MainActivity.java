package com.example.finalproject_unihaifa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register_button:
                startActivity(new Intent(getApplicationContext(), SignUp.class));
                break;
            case R.id.login_button:
                startActivity(new Intent(getApplicationContext(), LogIn.class));
                break;
        }
    }
}