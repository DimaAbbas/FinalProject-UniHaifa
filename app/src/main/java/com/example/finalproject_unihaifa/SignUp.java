package com.example.finalproject_unihaifa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity implements View.OnClickListener{
    EditText editName, editEmail, editPhone, editPassword, editDescription;
    RadioGroup rg;
    RadioButton userType;
    String Name, Email, Phone, Password, UserType, description;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, myAppointment;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editDescription = (EditText) findViewById(R.id.editDescription);
        rg = (RadioGroup) findViewById(R.id.radioGroup);

        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.backto).setOnClickListener(this);
        findViewById(R.id.radioButton1).setOnClickListener(this);
        findViewById(R.id.radioButton2).setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");
        myAppointment = database.getReference("Appointment Type");
    }

    private void registerUser() {
        Name = editName.getText().toString().trim();
        Phone = editPhone.getText().toString().trim();
        Email = editEmail.getText().toString().trim();
        Password = editPassword.getText().toString().trim();
        description = editDescription.getText().toString().trim();

        if (Name.isEmpty()) {
            editName.setError("Name is required!!");
            editName.requestFocus();
            return;
        }
        if (Phone.isEmpty()) {
            editPhone.setError("Phone is required!!");
            editPhone.requestFocus();
            return;
        }
        if (Phone.length() < 10) {
            editPhone.setError("Please provide valid phone number!");
            editPhone.requestFocus();
            return;
        }
        if (Email.isEmpty()) {
            editEmail.setError("Email is required!!");
            editEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            editEmail.setError("Please provide valid email");
            editEmail.requestFocus();
            return;
        }
        if (Password.isEmpty()) {
            editPassword.setError("Password is required!!");
            editPassword.requestFocus();
            return;
        }
        if (Password.length() < 6) {
            editPassword.setError("Min password length should be 6 characters!");
            editPassword.requestFocus();
            return;
        }
        if (rg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Please select how you would like to use the app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            userType = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
            UserType = userType.getText().toString().trim();

            if (UserType.equals("Business Owner") && description.isEmpty()) {
                editDescription.setError("Add business description");
                editDescription.requestFocus();
            }
        }

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getUid();
                            Query checkUser = myRef.child(uid).orderByChild("name").equalTo(Name);
                            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        Toast.makeText(getApplicationContext(), "User Registered Successfully", Toast.LENGTH_LONG).show();
                                        if (UserType.equals("Business Owner")) {
                                            user = new BusinessUser(Name, Phone, Email, Password, UserType, description);
                                            //myAppointment.child(Name).setValue(Name);
                                        } else {
                                            user = new User(Name, Phone, Email, Password, UserType);
                                        }
                                        myRef.child(uid).setValue(user);
                                        startActivity(new Intent(getApplicationContext(), LogIn.class));
                                    } else {
                                        editName.setError("This username exists, select another one!");
                                        editName.requestFocus();
                                        return;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to register! Change the email address", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register :
                registerUser();
                break;
            case R.id.backto:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.radioButton1:
                editDescription.setVisibility(editDescription.VISIBLE);
                break;
            case R.id.radioButton2:
                editDescription.setVisibility(editDescription.INVISIBLE);
                break;
        }
    }

}