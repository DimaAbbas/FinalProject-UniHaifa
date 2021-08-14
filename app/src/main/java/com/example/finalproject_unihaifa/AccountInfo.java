package com.example.finalproject_unihaifa;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AccountInfo extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView username_txt, accountType_txt, email_txt, phone_txt, pass_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        mAuth = FirebaseAuth.getInstance();

        username_txt = (TextView) findViewById(R.id.username_info);
        accountType_txt = (TextView) findViewById(R.id.account_type_info);
        email_txt = (TextView) findViewById(R.id.email_info);
        phone_txt = (TextView) findViewById(R.id.phone_info);
        pass_txt = (TextView) findViewById(R.id.password_info);

        username_txt.setText(LogIn.getUser().getName());
        accountType_txt.setText(LogIn.getUser().getType());
        email_txt.setText(LogIn.getUser().getEmail());
        phone_txt.setText(LogIn.getUser().getPhone());
        pass_txt.setText(LogIn.getUser().getPassword());

    }
}
