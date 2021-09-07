package com.example.finalproject_unihaifa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAppointmentType extends AppCompatActivity implements View.OnClickListener{

    EditText editName, editPrice;
    String name, price, username;
    String hoursStart, minutesStart, hoursEnd, minutesEnd, hoursDuration, minutesDuration;
    Spinner hoursStartS, minutesStartS, hoursEndS, minutesEndS, hoursDurationS, minutesDurationS;
    public Map<String, Boolean> days = new HashMap<>();
    ChipGroup chipgroup;
    double minDuration;

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
        editPrice = (EditText) findViewById(R.id.appointmentPrice);

        hoursStartS = (Spinner) findViewById(R.id.hoursStart);
        minutesStartS = (Spinner) findViewById(R.id.minutesStart);
        hoursEndS = (Spinner) findViewById(R.id.hoursEnd);
        minutesEndS = (Spinner) findViewById(R.id.minutesEnd);
        hoursDurationS = (Spinner) findViewById(R.id.hoursDuration);
        minutesDurationS = (Spinner) findViewById(R.id.minutesDuration);

        chipgroup = (ChipGroup) findViewById(R.id.chipGroup);

        setSpinners();

        FirebaseUser current = mAuth.getCurrentUser();

        if(current != null){
            myRef = myRef.child(current.getUid());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username = user.getName();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        findViewById(R.id.appointmentCreate).setOnClickListener(this);
    }

    private void setSpinners() {

        ArrayAdapter hour_adapter = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        hour_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursStartS.setAdapter(hour_adapter);
        hoursEndS.setAdapter(hour_adapter);
        hoursDurationS.setAdapter(hour_adapter);

        ArrayAdapter minute_adapter = ArrayAdapter.createFromResource(this,
                R.array.minutes, android.R.layout.simple_spinner_item);
        minute_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesStartS.setAdapter(minute_adapter);
        minutesEndS.setAdapter(minute_adapter);
        minutesDurationS.setAdapter(minute_adapter);
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
        price = editPrice.getText().toString().trim();
        hoursDuration = hoursDurationS.getSelectedItem().toString();
        minutesDuration = minutesDurationS.getSelectedItem().toString();
        hoursStart = hoursStartS.getSelectedItem().toString();
        minutesStart = minutesStartS.getSelectedItem().toString();
        hoursEnd = hoursEndS.getSelectedItem().toString();
        minutesEnd = minutesEndS.getSelectedItem().toString();

        if (name.isEmpty()){
            editName.setError("Appointment name is required !!");
            editName.requestFocus();
            return;
        }
        if (hoursDuration.equals("")) {
            TextView error = (TextView) hoursDurationS.getSelectedView();
            error.setError("please select hour");
            error.requestFocus();
            return;
        }
        if (minutesDuration.equals("")) {
            TextView error = (TextView) minutesDurationS.getSelectedView();
            error.setError("please select minutes");
            error.requestFocus();
            return;
        }
        if (hoursStart.equals("")) {
            TextView error = (TextView) hoursStartS.getSelectedView();
            error.setError("please select hour");
            error.requestFocus();
            return;
        }
        if (minutesStart.equals("")) {
            TextView error = (TextView) minutesStartS.getSelectedView();
            error.setError("please select minutes");
            error.requestFocus();
            return;
        }
        if (hoursEnd.equals("")) {
            TextView error = (TextView) hoursEndS.getSelectedView();
            error.setError("please select hour");
            error.requestFocus();
            return;
        }
        if (minutesEnd.equals("")) {
            TextView error = (TextView) minutesEndS.getSelectedView();
            error.setError("please select minutes");
            error.requestFocus();
            return;
        }
        if (price.isEmpty()) {
            editPrice.setError("Appointment price is required !!");
            editPrice.requestFocus();
            return;
        }

        Query checkUser = myAppointment.child(username).orderByChild("name").equalTo(name);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    getSelectedDays();
                    AppointmentType newApp = new AppointmentType(name, days, Integer.valueOf(price),
                            Integer.valueOf(hoursDuration), Integer.valueOf(minutesDuration),
                            Integer.valueOf(hoursStart), Integer.valueOf(minutesStart),
                            Integer.valueOf(hoursEnd), Integer.valueOf(minutesEnd));
                    myAppointment.child(username).child(name).setValue(newApp);
                    myAppointment.child(username).child(name).child("days").setValue(days);

                    double duration = newApp.getDuration_hours() + Double.valueOf(newApp.getDuration_minutes()) /60;
                    myRef.child("minDuration").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.getValue(double.class) > duration)
                                    myRef.child("minDuration").setValue(duration);
                            } else myRef.child("minDuration").setValue(duration);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    finish();
                    //startActivity(new Intent(getApplicationContext(), AppointmentsSettings.class));
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

    private void getSelectedDays(){
        days.put("sun", false);
        days.put("mon", false);
        days.put("tue", false);
        days.put("wed", false);
        days.put("thu", false);
        days.put("fri", false);
        days.put("sat", false);

        List<Integer> ids = chipgroup.getCheckedChipIds();
         for (Integer id:ids) {
             switch (id) {
                 case R.id.sun:
                     days.put("sun", true); break;
                 case R.id.mon:
                     days.put("mon", true); break;
                 case R.id.tue:
                     days.put("tue", true); break;
                 case R.id.wed:
                     days.put("wed", true); break;
                 case R.id.thu:
                     days.put("thu", true); break;
                 case R.id.fri:
                     days.put("fri", true); break;
                 case R.id.sat:
                     days.put("sat", true); break;
             }
         }
    }
}
