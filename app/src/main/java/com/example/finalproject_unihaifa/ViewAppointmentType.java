package com.example.finalproject_unihaifa;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAppointmentType extends AppCompatActivity implements View.OnClickListener{

    String passedName, name, price, username;
    EditText editName, editPrice;
    String startH, startM, endH, endM, durationH, durationM;
    Spinner startHS, startMS, endHS, endMS, durationHS, durationMS;
    ChipGroup chipGroup;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    ArrayAdapter hour_adapter, minute_adapter;

    public Map<String, Boolean> days = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_type);

        Bundle b = getIntent().getExtras();
        passedName = b.getString("appName");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Appointment Type");

        username = LogIn.getUser().getName();

        System.out.println(passedName);
        System.out.println(username);

        editName = (EditText) findViewById(R.id.editAppointmentName);
        editPrice = (EditText) findViewById(R.id.editAppointmentPrice);

        durationHS = (Spinner) findViewById(R.id.editHoursDuration);
        durationMS = (Spinner) findViewById(R.id.editMinutesDuration);
        startHS = (Spinner) findViewById(R.id.editHoursStart);
        startMS = (Spinner) findViewById(R.id.editMinutesStart);
        endHS = (Spinner) findViewById(R.id.editHoursEnd);
        endMS = (Spinner) findViewById(R.id.editMinutesEnd);
        chipGroup = (ChipGroup) findViewById(R.id.editChipGroup);

        setSpinners();

        findViewById(R.id.editSwitch).setOnClickListener(this);

        myRef = myRef.child(username).child(passedName);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    getAppInfo(snapshot);
                    setCheckedDays(snapshot);
                }else {
                    System.out.println("snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAppInfo(DataSnapshot snapshot) {
        if (snapshot.exists()) {
            AppointmentType appointmentType = snapshot.getValue(AppointmentType.class);
            editName.setText(appointmentType.getName());
            editPrice.setText(String.valueOf(appointmentType.getPrice()));

            durationH = get2Digit(appointmentType.getDuration_hours());
            durationHS.setSelection(hour_adapter.getPosition(durationH));

            durationM = get2Digit(appointmentType.getDuration_minutes());
            durationMS.setSelection(minute_adapter.getPosition(durationM));

            startH = get2Digit(appointmentType.getStartTime_hours());
            startHS.setSelection(hour_adapter.getPosition(startH));

            startM = get2Digit(appointmentType.getStartTime_minutes());
            startMS.setSelection(minute_adapter.getPosition(startM));

            endH = get2Digit(appointmentType.getEndTime_hours());
            endHS.setSelection(hour_adapter.getPosition(endH));

            endM = get2Digit(appointmentType.getEndTime_minutes());
            endMS.setSelection(minute_adapter.getPosition(endM));
        }
    }

    private void setCheckedDays(DataSnapshot snapshot) {
        if (snapshot.exists()) {
            if (snapshot.child("days").child("sun").getValue(Boolean.class))
                ((Chip) findViewById(R.id.editSun)).setChecked(true);
            if (snapshot.child("days").child("mon").getValue(Boolean.class))
                ((Chip) findViewById(R.id.editMon)).setChecked(true);
            if (snapshot.child("days").child("tue").getValue(Boolean.class))
                ((Chip) findViewById(R.id.editTue)).setChecked(true);
            if (snapshot.child("days").child("wed").getValue(Boolean.class))
                ((Chip) findViewById(R.id.editWed)).setChecked(true);
            if (snapshot.child("days").child("thu").getValue(Boolean.class))
                ((Chip) findViewById(R.id.editThu)).setChecked(true);
            if (snapshot.child("days").child("fri").getValue(Boolean.class))
                ((Chip) findViewById(R.id.editFri)).setChecked(true);
            if (snapshot.child("days").child("sat").getValue(Boolean.class))
                ((Chip) findViewById(R.id.editSat)).setChecked(true);
        }
    }

    private void setSpinners() {

        hour_adapter = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        hour_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startHS.setAdapter(hour_adapter);
        endHS.setAdapter(hour_adapter);
        durationHS.setAdapter(hour_adapter);
        startHS.setEnabled(false);
        endHS.setEnabled(false);
        durationHS.setEnabled(false);

        minute_adapter = ArrayAdapter.createFromResource(this,
                R.array.minutes, android.R.layout.simple_spinner_item);
        minute_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startMS.setAdapter(minute_adapter);
        endMS.setAdapter(minute_adapter);
        durationMS.setAdapter(minute_adapter);
        startMS.setEnabled(false);
        endMS.setEnabled(false);
        durationMS.setEnabled(false);
    }

    private void setEditing(boolean bool){
        editName.setEnabled(bool);
        editPrice.setEnabled(bool);

        durationHS.setEnabled(bool);
        startHS.setEnabled(bool);
        endHS.setEnabled(bool);
        durationMS.setEnabled(bool);
        startMS.setEnabled(bool);
        endMS.setEnabled(bool);

        ((Chip) findViewById(R.id.editSun)).setClickable(bool);
        ((Chip) findViewById(R.id.editMon)).setClickable(bool);
        ((Chip) findViewById(R.id.editTue)).setClickable(bool);
        ((Chip) findViewById(R.id.editWed)).setClickable(bool);
        ((Chip) findViewById(R.id.editThu)).setClickable(bool);
        ((Chip) findViewById(R.id.editFri)).setClickable(bool);
        ((Chip) findViewById(R.id.editSat)).setClickable(bool);

        if (bool)
            ((Switch) findViewById(R.id.editSwitch)).setText("save");
        else ((Switch) findViewById(R.id.editSwitch)).setText("edit");

    }

    private void editAppointmentType() {
        name = editName.getText().toString().trim();
        price = editPrice.getText().toString().trim();
        durationH = durationHS.getSelectedItem().toString();
        durationM = durationMS.getSelectedItem().toString();
        startH = startHS.getSelectedItem().toString();
        startM = startMS.getSelectedItem().toString();
        endH = endHS.getSelectedItem().toString();
        endM = endMS.getSelectedItem().toString();

        if (name.isEmpty()){
            editName.setError("Appointment name is required !!");
            editName.requestFocus();
            return;
        }
        if (durationH.equals("")) {
            TextView error = (TextView) durationHS.getSelectedView();
            error.setError("please select hour");
            error.requestFocus();
            return;
        }
        if (durationM.equals("")) {
            TextView error = (TextView) durationMS.getSelectedView();
            error.setError("please select minutes");
            error.requestFocus();
            return;
        }
        if (startH.equals("")) {
            TextView error = (TextView) startHS.getSelectedView();
            error.setError("please select hour");
            error.requestFocus();
            return;
        }
        if (startM.equals("")) {
            TextView error = (TextView) startMS.getSelectedView();
            error.setError("please select minutes");
            error.requestFocus();
            return;
        }
        if (endH.equals("")) {
            TextView error = (TextView) endHS.getSelectedView();
            error.setError("please select hour");
            error.requestFocus();
            return;
        }
        if (endM.equals("")) {
            TextView error = (TextView) endMS.getSelectedView();
            error.setError("please select minutes");
            error.requestFocus();
            return;
        }
        if (price.isEmpty()) {
            editPrice.setError("Appointment price is required !!");
            editPrice.requestFocus();
            return;
        }

        myRef = database.getReference("Appointment Type").child(username);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    getSelectedDays();
                    AppointmentType newApp = new AppointmentType(name, days, Integer.valueOf(price),
                            Integer.valueOf(durationH), Integer.valueOf(durationM),
                            Integer.valueOf(startH), Integer.valueOf(startM),
                            Integer.valueOf(endH), Integer.valueOf(endM));
                    myRef.child(passedName).removeValue();
                    passedName = name;
                    myRef.child(name).setValue(newApp);
                    myRef.child(name).child("days").setValue(days);
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

        List<Integer> ids = chipGroup.getCheckedChipIds();
        for (Integer id:ids) {
            switch (id) {
                case R.id.editSun:
                    days.put("sun", true); break;
                case R.id.editMon:
                    days.put("mon", true); break;
                case R.id.editTue:
                    days.put("tue", true); break;
                case R.id.editWed:
                    days.put("wed", true); break;
                case R.id.editThu:
                    days.put("thu", true); break;
                case R.id.editFri:
                    days.put("fri", true); break;
                case R.id.editSat:
                    days.put("sat", true); break;
            }
        }
    }

    private String get2Digit(int no){
        String x = String.valueOf(no);
        if (x.length() == 1)
            x = "0" + x;
        return x;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editSwitch:
                if (((Switch)findViewById(R.id.editSwitch)).isChecked())
                    setEditing(true);
                else {
                    setEditing(false);
                    editAppointmentType();
                    System.out.println("save clicked");
                }
        }
    }
}
