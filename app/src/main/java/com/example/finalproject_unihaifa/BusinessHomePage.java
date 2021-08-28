package com.example.finalproject_unihaifa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupMenu;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BusinessHomePage extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef, appRef, myApp;

    String userName;
    TextView userNameView;

    ArrayList<String> fullName = new ArrayList<>();
    ArrayList<String> appointments = new ArrayList<>();
    ArrayList<String> customers = new ArrayList<>();
    ArrayList<String> phones = new ArrayList<>();
    ArrayList<String> hours = new ArrayList<>();
    ArrayList<String> minutes = new ArrayList<>();

    DailyBookedAppsAdapter adapter;
    ListView dailyList;

    CalendarView calendarView;
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    String currentDate, selectedDate;
    int currentYear, currentMonth, currentDay;
    int selectedYear, selectedMonth, selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("User");
        appRef = database.getReference("Appointments");
        myApp = database.getReference("Appointment Type");

        dailyList = (ListView) findViewById(R.id.daily_booked_apps_list);
        adapter = new DailyBookedAppsAdapter(this, fullName, appointments, customers, phones, hours, minutes);
        dailyList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        currentDate = df.format(Calendar.getInstance().getTime());
        currentYear = Integer.parseInt(currentDate.substring(6,10));
        currentMonth = Integer.parseInt(currentDate.substring(3,5));
        currentDay = Integer.parseInt(currentDate.substring(0,2));

        userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getting the user's name to show on top of screen
                userName = snapshot.getValue(User.class).getName();
                userNameView = (TextView) findViewById(R.id.BusinessUserNameText);
                userNameView.setText(userName);

                filterAppointments(userName);
                selectedYear = currentYear;
                selectedMonth = currentMonth;
                selectedDay = currentDay;
                getBookedAppointments(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        calendarView = (CalendarView) findViewById(R.id.business_home_calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = df.format(new Date(year-1900, month, dayOfMonth));
                selectedYear = Integer.parseInt(selectedDate.substring(6,10));
                selectedMonth = Integer.parseInt(selectedDate.substring(3,5));
                selectedDay = Integer.parseInt(selectedDate.substring(0,2));

                userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName = snapshot.getValue(User.class).getName();
                        getBookedAppointments(userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        findViewById(R.id.B_account).setOnClickListener(this);
        findViewById(R.id.B_new_appointment).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.B_account){
            //Toast.makeText(getApplicationContext(), "Account button clicked", Toast.LENGTH_LONG).show();
            showPopup(view);
        }
        else if (view.getId() == R.id.B_new_appointment){
            //Toast.makeText(getApplicationContext(), "New Appointment button clicked", Toast.LENGTH_LONG).show();
            userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userName = snapshot.getValue(User.class).getName();
                    myApp.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getChildrenCount() != 0)
                                startActivity(new Intent(getApplicationContext(), BusinessBooking.class));
                            else
                                Toast.makeText(getApplicationContext(), "you still didn't created an appointments type, you can't book one now!", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.business_submenu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.B_account_info:
                        //Toast.makeText(getApplicationContext(), "Your account clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AccountInfo.class));
                        break;
                    case R.id.B_Appointments:
                        //Toast.makeText(getApplicationContext(), "appointments settings clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AppointmentsSettings.class));
                        break;
                    case R.id.B_requests:
                        startActivity(new Intent(getApplicationContext(), CustomerRequests.class));
                        break;
                    case R.id.customers_list:
                        //Toast.makeText(getApplicationContext(), "customer list clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), FeaturedCustomers.class));
                        break;

                    case R.id.log_out:{
                        //Toast.makeText(getApplicationContext(), "log out clicked", Toast.LENGTH_SHORT).show();
                        Dialog dialog = new Dialog(BusinessHomePage.this);
                        dialog.setContentView(R.layout.dialog_logout);
                        dialog.setTitle("lou out");

                        ((Button) dialog.findViewById(R.id.logout_dialog)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
                                dialog.dismiss();
                                finish();
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

                    default:
                        break;
                }

                return true;
            }
        });

    }

    private void getBookedAppointments(String userName) {
        appRef.orderByChild("businessN").equalTo(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fullName.clear(); appointments.clear(); customers.clear();
                    phones.clear(); hours.clear(); minutes.clear();
                    adapter.notifyDataSetChanged();
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Appointment app = ds.getValue(Appointment.class);
                        int year = Integer.parseInt(app.getDate().substring(6,10));
                        int month = Integer.parseInt(app.getDate().substring(3,5));
                        int day = Integer.parseInt(app.getDate().substring(0,2));

                        if (year == selectedYear && month == selectedMonth && day == selectedDay){
                            fullName.add(ds.getKey());
                            appointments.add(app.getType());
                            customers.add(app.getCustomerN());
                            phones.add(app.getCustomerPhone());
                            hours.add(app.getStartTime().substring(0,2));
                            minutes.add(app.getStartTime().substring(3,5));
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*appRef.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    appointments.clear(); customers.clear(); phones.clear();
                    hours.clear(); minutes.clear();
                    adapter.notifyDataSetChanged();
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Appointment app = ds.getValue(Appointment.class);
                        int year = Integer.parseInt(app.getDate().substring(6,10));
                        int month = Integer.parseInt(app.getDate().substring(3,5));
                        int day = Integer.parseInt(app.getDate().substring(0,2));

                        if (year == currentYear && month == currentMonth && day == currentDay){
                            fullName.add(ds.getKey());
                            System.out.println(ds.getKey());
                            appointments.add(app.getType());
                            customers.add(app.getCustomerN());
                            phones.add("0537756048");
                            hours.add(app.getStartTime().substring(0,2));
                            minutes.add(app.getStartTime().substring(3,5));
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    private void filterAppointments(String userName) {
        appRef.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Appointment appointment = ds.getValue(Appointment.class);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
