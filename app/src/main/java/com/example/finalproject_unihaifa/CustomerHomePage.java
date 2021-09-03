package com.example.finalproject_unihaifa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerHomePage extends AppCompatActivity implements View.OnClickListener {
    TextView username;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, myApp;
    static User user = new User();
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    ArrayList<String> fullName = new ArrayList<>();
    ArrayList<String> appointments = new ArrayList<>();
    ArrayList<String> business = new ArrayList<>();
    ArrayList<String> phones = new ArrayList<>();
    ArrayList<String> hours = new ArrayList<>();
    ArrayList<String> minutes = new ArrayList<>();

    DailyBookedAppsAdapter adapter;
    ListView dailyList;

    CalendarView calendarView;
    String currentDate, selectedDate;
    int currentYear, currentMonth, currentDay;
    int selectedYear, selectedMonth, selectedDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");
        myApp = database.getReference("Appointments");

        username = (TextView) findViewById(R.id.username1);

        dailyList = (ListView) findViewById(R.id.Appointments);
        adapter = new DailyBookedAppsAdapter(this, fullName, appointments, business, phones, hours, minutes);
        dailyList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        currentDate = df.format(Calendar.getInstance().getTime());
        currentYear = Integer.parseInt(currentDate.substring(6,10));
        currentMonth = Integer.parseInt(currentDate.substring(3,5));
        currentDay = Integer.parseInt(currentDate.substring(0,2));

        myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getting the user's name to show on top of screen
                User user_ = snapshot.getValue(User.class);
                setUser(user_);
                username.setText(user_.getName() + " ,");

                filterAppointments(user_.getName());
                selectedYear = currentYear;
                selectedMonth = currentMonth;
                selectedDay = currentDay;
                getBookedAppointments(user_.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        calendarView = (CalendarView) findViewById(R.id.customer_home);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = df.format(new Date(year-1900, month, dayOfMonth));
                selectedYear = Integer.parseInt(selectedDate.substring(6,10));
                selectedMonth = Integer.parseInt(selectedDate.substring(3,5));
                selectedDay = Integer.parseInt(selectedDate.substring(0,2));

                getBookedAppointments(getUser().getName());
            }
        });

        findViewById(R.id.B_account).setOnClickListener(this);
        findViewById(R.id.B_new_appointment).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.B_account:
                showPopup(v);
                break;
            case R.id.B_new_appointment:
                startActivity(new Intent(getApplicationContext(), SearchResult.class));
                break;
        }
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.customer_submenu, popup.getMenu());

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.C_account_info:
                        //Toast.makeText(getApplicationContext(), "Your account clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AccountInfo.class));
                        break;

                    case R.id.log_out1:
                        Toast.makeText(getApplicationContext(), "log out clicked", Toast.LENGTH_SHORT).show();
                        Dialog dialog = new Dialog(CustomerHomePage.this);
                        dialog.setContentView(R.layout.dialog_logout);
                        dialog.setTitle("lou out");

                        ((Button) dialog.findViewById(R.id.logout_dialog)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAuth.signOut();
                                dialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        });

                        ((Button) dialog.findViewById(R.id.cancel_logout)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        dialog.getWindow().setLayout(1000, 600);
                        break;
                }

                return true;
            }
        });

    }

    private void getBookedAppointments(String userName) {
        myApp.orderByChild("customerN").equalTo(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fullName.clear(); appointments.clear(); business.clear();
                    phones.clear(); hours.clear(); minutes.clear();
                    adapter.notifyDataSetChanged();
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Appointment app = ds.getValue(Appointment.class);
                        if(app.getIsCustomer().equals("true")){
                            int year = Integer.parseInt(app.getDate().substring(6,10));
                            int month = Integer.parseInt(app.getDate().substring(3,5));
                            int day = Integer.parseInt(app.getDate().substring(0,2));

                            if (year == selectedYear && month == selectedMonth && day == selectedDay){
                                fullName.add(ds.getKey());
                                appointments.add(app.getType());
                                business.add(app.getBusinessN());
                                phones.add(app.getBusinessPhone());
                                hours.add(app.getStartTime().substring(0,2));
                                minutes.add(app.getStartTime().substring(3,5));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterAppointments(String userName) {
        myApp.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Appointment appointment = ds.getValue(Appointment.class);
                        if(appointment.getIsCustomer().equals("true")){
                            int year = Integer.parseInt(appointment.getDate().substring(6,10));
                            int month = Integer.parseInt(appointment.getDate().substring(3,5));
                            int day = Integer.parseInt(appointment.getDate().substring(0,2));

                            if (currentYear > year)
                                ds.getRef().removeValue();
                            else if (currentYear == year && currentMonth > month)
                                ds.getRef().removeValue();
                            else if (currentYear == year && currentMonth == month && currentDay > day)
                                ds.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setUser(User user){
        this.user = user;
    }
    public static User getUser(){
        return user;
    }
}
