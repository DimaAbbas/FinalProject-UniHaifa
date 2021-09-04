package com.example.finalproject_unihaifa;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountInfo extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    TextView username_txt, accountType_txt, email_txt, phone_txt, description_txt, description_view;
    String description, phone, userType;
    EditText edit_phone, edit_description;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("User");

        username_txt = (TextView) findViewById(R.id.username_info);
        accountType_txt = (TextView) findViewById(R.id.account_type_info);
        email_txt = (TextView) findViewById(R.id.email_info);
        phone_txt = (TextView) findViewById(R.id.phone_info);
        description_txt = (TextView) findViewById(R.id.description_info);

        edit_phone = (EditText) findViewById(R.id.edit_phone_info);
        edit_description = (EditText) findViewById(R.id.edit_description_info);
        description_view = (TextView) findViewById(R.id.description);

        findViewById(R.id.editAccountSwitch).setOnClickListener(this);
        findViewById(R.id.reset_password).setOnClickListener(this);


        myRef = myRef.child(mAuth.getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username_txt.setText(snapshot.getValue(User.class).getName());
                accountType_txt.setText(snapshot.getValue(User.class).getType());
                email_txt.setText(snapshot.getValue(User.class).getEmail());
                phone_txt.setText(snapshot.getValue(User.class).getPhone());

                edit_phone.setText(phone_txt.getText().toString().trim());

                if (snapshot.getValue(BusinessUser.class).getType().equals("Business Owner")) {
                    description_txt.setText(snapshot.getValue(BusinessUser.class).getDescription());
                    edit_description.setText(description_txt.getText().toString().trim());
                    description_view.setVisibility(View.VISIBLE);
                    description_txt.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editAccountSwitch:
                if (((Switch)findViewById(R.id.editAccountSwitch)).isChecked()) {
                    phone_txt.setVisibility(View.INVISIBLE);
                    edit_phone.setVisibility(View.VISIBLE);
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue(BusinessUser.class).getType().equals("Business Owner")){
                                description_txt.setVisibility(View.INVISIBLE);
                                edit_description.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    ((Switch)findViewById(R.id.editAccountSwitch)).setText("save");
                } else {
                    phone_txt.setVisibility(View.VISIBLE);
                    edit_phone.setVisibility(View.INVISIBLE);
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue(BusinessUser.class).getType().equals("Business Owner")){
                                description_txt.setVisibility(View.VISIBLE);
                                edit_description.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    editAccountInfo();
                    ((Switch)findViewById(R.id.editAccountSwitch)).setText("edit");
                }
                break;
            case R.id.reset_password:
                resetPassword();
                break;
        }
    }

    private void editAccountInfo() {
        phone = edit_phone.getText().toString().trim();
        description = edit_description.getText().toString().trim();

        if (phone.isEmpty()) {
            edit_phone.setError("Phone is required!!");
            edit_phone.requestFocus();
            return;
        }
        if (phone.length() < 10) {
            edit_phone.setError("Please provide valid phone number!");
            edit_phone.requestFocus();
            return;
        }

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.child("phone").setValue(phone);

                if (snapshot.getValue(BusinessUser.class).getType().equals("Business Owner")){
                    if (description.isEmpty()) {
                        edit_description.setError("you must enter a description to your business");
                        edit_description.requestFocus();
                        return;
                    }
                    myRef.child("description").setValue(description);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void resetPassword() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String reset_email = (snapshot.getValue(User.class)).getEmail();

                mAuth.sendPasswordResetEmail(reset_email);
                Toast.makeText(getApplicationContext(), "A reset email was sent to your email address", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
