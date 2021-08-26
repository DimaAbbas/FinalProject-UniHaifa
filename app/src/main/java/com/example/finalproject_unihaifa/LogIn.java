package com.example.finalproject_unihaifa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView forget;
    EditText email, pass;
    String Email, Pass, userType;
    static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");

        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.userpass);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.txtForget).setOnClickListener(this);
        findViewById(R.id.backto2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_login:
                LogIn();
                break;
            case R.id.backto2:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.txtForget:
                resetPassword();
                break;
        }
    }

    public void LogIn() {
        Email = email.getText().toString().trim();
        Pass = pass.getText().toString().trim();

        if(Email.isEmpty()){
            email.setError("Username is required!!");
            email.requestFocus();
            return;
        }
        if(Pass.isEmpty()){
            pass.setError("Password is required!!");
            pass.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Query checkUser = myRef.child(mAuth.getUid());
                            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        user = snapshot.getValue(User.class);
                                        userType = user.getType();
                                        System.out.println(userType);
                                        Toast.makeText(getApplicationContext(), "LogIn Successful", Toast.LENGTH_LONG).show();
                                        if (userType.equals("Business Owner")) {
                                            startActivity(new Intent(getApplicationContext(), BusinessHomePage.class));
                                        }
                                        else {
                                            startActivity(new Intent(getApplicationContext(), CustomerHomePage.class));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public static User getUser() {
        return user;
    }
    public static void setUser(User u){
        user=new User(u);
    }

    private void resetPassword() {

        Email = email.getText().toString().trim();
        if (Email.isEmpty()) {
            email.setError("Enter your email");
            email.requestFocus();
            return;
        }

        myRef.orderByChild("email").equalTo(Email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    email.setError("this email is not registered at our app");
                    email.requestFocus();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAuth.sendPasswordResetEmail(Email);
        Toast.makeText(getApplicationContext(), "reset email sent", Toast.LENGTH_SHORT).show();
    }
}

