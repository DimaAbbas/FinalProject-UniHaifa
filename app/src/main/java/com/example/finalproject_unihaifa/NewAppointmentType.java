package com.example.finalproject_unihaifa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class NewAppointmentType extends AppCompatActivity implements View.OnClickListener{

    EditText editName, editDuration, editStart, editEnd, editPrice;
    String name, duration, start, end, price, username;
    User user;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, myAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment_type);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");
        myAppointment = database.getReference("Appointment Type");
        editName = (EditText) findViewById(R.id.appointmentName);
        editDuration = (EditText) findViewById(R.id.appointmentDuration);
        editStart = (EditText) findViewById(R.id.appointmentStart);
        editEnd = (EditText) findViewById(R.id.appointmentEnd);
        editPrice = (EditText) findViewById(R.id.appointmentPrice);
        FirebaseUser current = mAuth.getCurrentUser();
        if(current != null){
            username = myRef.child(current.getUid()).child("name").toString();
        }

        findViewById(R.id.appointmentCreate).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.appointmentCreate:
                CreateAppointment();
        }
    }

    private void CreateAppointment() {
        name = editName.getText().toString().trim();
        duration = editDuration.getText().toString().trim();
        start = editStart.getText().toString().trim();
        end = editEnd.getText().toString().trim();
        price = editPrice.getText().toString().trim();

        if (name.isEmpty()){
            editName.setError("Appointment name is required !!");
            editName.requestFocus();
            return;
        }
        if (duration.isEmpty()) {
            editDuration.setError("Appointment duration is required !!");
            editDuration.requestFocus();
            return;
        }
        if (start.isEmpty()) {
            editStart.setError("first appointment time is required !!");
            editStart.requestFocus();
            return;
        }
        if (end.isEmpty()) {
            editEnd.setError("last appointment time is required !!");
            editEnd.requestFocus();
            return;
        }
        if (price.isEmpty()) {
            editPrice.setError("Appointment price is required !!");
            editPrice.requestFocus();
            return;
        }

        Query checkUser = myAppointment.child(username).orderByChild("TypeName").equalTo(name);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    AppointmentType newApp = new AppointmentType(name, Integer.valueOf(duration),
                            Integer.valueOf(start), Integer.valueOf(end), Integer.valueOf(price));
                    myAppointment.child(username).child(name).setValue(newApp);
                    ((BusinessUser) LogIn.getUser()).addAppointmentType(newApp);
                }
                else{
                    editName.setError("This appointment type name exists, select another one!");
                    editName.requestFocus();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
