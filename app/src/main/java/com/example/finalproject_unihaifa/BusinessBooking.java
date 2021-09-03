package com.example.finalproject_unihaifa;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessBooking extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myref, appref, myApp;

    String[] appType;
    ArrayAdapter<String> adapter;
    List<String> list;
    Spinner spinner;

    CalendarView calendar;
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    Date selectDate = null;
    String[] days = { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
    ArrayList<String> options = new ArrayList<String>();
    HashMap<Double,Double> booking = new HashMap<Double,Double>();
    HashMap<String,Boolean> daysMap = new HashMap<String,Boolean>();
    AppointmentType select = null;
    String bu;
    ListView availableApps;
    BusinessBookingListAdapter adapter1;
    Date currentDate;

    static String selectedType, date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_booking);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference("User");
        appref = database.getReference("Appointment Type");
        myApp = database.getReference("Appointments");

        list = new ArrayList<String>();

        spinner = (Spinner) findViewById(R.id.appTypeSpinner);

        availableApps = ((ListView) findViewById(R.id.business_booking_list));
        adapter1 = new BusinessBookingListAdapter(this, options);
        availableApps.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();

        myref.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String current = snapshot.getValue(User.class).getName();
                appref.child(current).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            list.clear();
                            list.add("-select type-");
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                list.add(ds.getValue(AppointmentType.class).getName());
                            }

                            adapter = new ArrayAdapter<String>(BusinessBooking.this, android.R.layout.simple_spinner_item, list);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        }
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

        currentDate = Calendar.getInstance().getTime();
        calendar = (CalendarView) findViewById(R.id.calendar_business_booking);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String cd = df.format(currentDate);

                options.clear();
                booking.clear();
                adapter1.notifyDataSetChanged();

                if(Integer.parseInt(cd.substring(6,10)) > year || Integer.parseInt(cd.substring(3,5)) > (month+1)
                        ||(Integer.parseInt(cd.substring(0,2)) > dayOfMonth && Integer.parseInt(cd.substring(3,5)) == (month+1)))
                    Toast.makeText(getApplicationContext(), "You have selected an old day, select another date", Toast.LENGTH_SHORT).show();

                else {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth);
                    int day  = c.get(Calendar.DAY_OF_WEEK);
                    selectDate = new Date(year-1900,month,dayOfMonth);
                    cd = df.format(selectDate);
                    setDate(cd);
                    CheckAppointment(cd, days[day-1]);
                }
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                options.clear();
                booking.clear();
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("NewApi")
    public void CheckAppointment(String cd, String day){


        myref.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String current = snapshot.getValue(User.class).getName();
                String selectedAppType = spinner.getSelectedItem().toString();
                if (selectedAppType != "-select type-") {
                    setSelectedType(selectedAppType);
                    appref.child(current).child(selectedAppType).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                select = snapshot.getValue(AppointmentType.class);
                                for(int i = 0; i < 7; i++){
                                    setDaysMap(days[i],
                                            snapshot.child("days").child(days[i]).getValue(Boolean.class));
                                }
                                if(daysMap.get(day) == false){
                                    options.clear();
                                    adapter1.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(), "No such booking received in this day", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Query query = myApp.orderByChild("businessN").equalTo(current);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot i : snapshot.getChildren()){
                                                if(i.exists()){
                                                    Appointment p = i.getValue(Appointment.class);
                                                    if(p.getDate().equals(cd)){
                                                        double h = Double.parseDouble((String) p.getStartTime().subSequence(0,2)) +
                                                                Double.parseDouble((String) p.getStartTime().subSequence(3,5)) / 60.0;
                                                        double h1 = Double.parseDouble((String) p.getEndTime().subSequence(0,2)) +
                                                                Double.parseDouble((String) p.getEndTime().subSequence(3,5)) / 60.0;

                                                        booking.put(h,h1);
                                                    }
                                                }
                                            }
                                            double d = select.getDuration_hours() + Double.valueOf(select.getDuration_minutes())/60;
                                            double s = select.getStartTime_hours() + Double.valueOf(select.getStartTime_minutes())/60;
                                            double e = select.getEndTime_hours() + Double.valueOf(select.getEndTime_minutes())/60;

                                            for(double i = s; i <= e; i++){
                                                double j = i, v;
                                                int h = (int) j; int m = (int) ((j%1) * 60.0);
                                                int h1 = (int) (j+d); int m1 = (int) (((j+d)%1)*60.0);
                                                Time t1 = new Time(h,m,0);
                                                Time t2 = new Time(h1,m1,0);
                                                String str = "from " + t1.toString().subSequence(0,5) +
                                                        " to " + t2.toString().subSequence(0,5);
                                                if(!booking.isEmpty()){
                                                    for(double k : booking.keySet()){
                                                        v = booking.get(k);
                                                        if((j <= k && k < (j+d)) || (j < v && v <= (j+d)) || (k <= j && (j+d) <= v)){
                                                            if(options.contains(str)) {
                                                                options.remove(str);
                                                                adapter1.notifyDataSetChanged();
                                                            }
                                                            break;
                                                        }
                                                        else if(!options.contains(str)) {
                                                            options.add(str);
                                                            adapter1.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                                else {
                                                    options.add(str);
                                                    adapter1.notifyDataSetChanged();
                                                }
                                            }
                                            booking.clear();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else Toast.makeText(getApplicationContext(), "Select the type of the appointment", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setDaysMap(String day, Boolean tf){
        daysMap.put(day,tf);
    }

    public void setSelectedType(String s) {
        selectedType = s;
    }
    public static String getSelectedType(){
        return selectedType;
    }
    public void setDate(String d) {
        date = d;
    }
    public static String getDate() {
        return date;
    }
}
