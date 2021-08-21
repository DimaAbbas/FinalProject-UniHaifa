package com.example.finalproject_unihaifa;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.SimpleFormatter;

public class Booking extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    TextView txt;
    GridView gridView;
    BusinessUser user;
    String name, newtxt;
    //BusinessUser bu;
    String bu;
    CalendarView calendar;
    ArrayList<String> typesList = new ArrayList<String>();
    ArrayList<String> options = new ArrayList<String>();
    HashMap<Double,Double> booking = new HashMap<Double,Double>();
    ListPopupWindow statusPopupList;
    AppCompatEditText types;
    static AppointmentType select = null;
    static Date selectDate = null;
    Appointment app;
    String[] days = { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myApp, myRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myApp = database.getReference("Appointments");
        myRef = database.getReference("Appointment Type");

        bu = getIntent().getExtras().getString("businessUser");

        gridView = (GridView) findViewById(R.id.gridView);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background));

        types = (AppCompatEditText) findViewById(R.id.types);
        setPopupList();
        setListeners();


        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        txt = (TextView) findViewById(R.id.textView13);
        name = bu;
        newtxt = txt.getText().toString().trim();
        newtxt += " " + name;
        txt.setText(newtxt);

        findViewById(R.id.btn).setOnClickListener(this);

        calendar = (CalendarView) findViewById(R.id.calendarView2);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Date currentDate = Calendar.getInstance().getTime();
                String cd = df.format(currentDate);

                if(Integer.parseInt(cd.substring(6,10)) > year || Integer.parseInt(cd.substring(3,5)) > (month+1)
                        ||(Integer.parseInt(cd.substring(0,2)) > dayOfMonth && Integer.parseInt(cd.substring(3,5)) == (month+1)))
                    Toast.makeText(getApplicationContext(), "You have selected an old day, select another date", Toast.LENGTH_SHORT).show();

                else {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth);
                    int day  = c.get(Calendar.DAY_OF_WEEK);
                    selectDate = new Date(year-1900,month,dayOfMonth);
                    cd = df.format(selectDate);
                    CheckAppointment(cd, days[day-1]);
                }
            }
        });
    }

    private void setListeners() {
        findViewById(R.id.types).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusPopupList.show();
            }
        });
    }

    private void setPopupList() {
        Query query = myRef.child(bu);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.getChildren()){
                    if(i.exists()){
                        AppointmentType app = i.getValue(AppointmentType.class);
                        typesList.add(app.getName());
                    }
                    statusPopupList = new ListPopupWindow(Booking.this);
                    ArrayAdapter adapter = new ArrayAdapter<String>(Booking.this, R.layout.list_item, R.id.type, typesList);
                    statusPopupList.setAnchorView(types); //this let as set the popup below the EditText
                    statusPopupList.setAdapter(adapter);
                    statusPopupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            types.setText(parent.getItemAtPosition(position).toString());//we set the selected element in the EditText
                            Query q = myRef.child(bu).child("name").equalTo(parent.getItemAtPosition(position).toString());
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        AppointmentType t = snapshot.getValue(AppointmentType.class);
                                        setSelectedType(t);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            statusPopupList.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NewApi")
    public void CheckAppointment(String currentDate, String day){
        booking.clear();
        options.clear();
        if(select != null){
            if(select.Days().get(day) == false)
                Toast.makeText(getApplicationContext(), "No such booking received today", Toast.LENGTH_SHORT).show();
            else {
                Query query = myApp.child(bu);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot i : snapshot.getChildren()){
                            if(i.exists()){
                                Appointment p = i.getValue(Appointment.class);
                                if(p.getDate().equals(currentDate)){
                                    double h = Double.parseDouble((String) p.getStartTime().toString().subSequence(0,2)) +
                                            Double.parseDouble((String) p.getStartTime().toString().subSequence(3,5)) / 60.0;
                                    double h1 = Double.parseDouble((String) p.getEndTime().toString().subSequence(0,2)) +
                                            Double.parseDouble((String) p.getEndTime().toString().subSequence(3,5)) / 60.0;

                                    booking.put(h,h1);
                                }
                            }
                        }
                        double d = select.getDuration_hours() + Double.valueOf(select.getDuration_minutes())/60;
                        double s = select.getStartTime_hours() + Double.valueOf(select.getStartTime_minutes())/60;
                        double e = select.getEndTime_hours() + Double.valueOf(select.getEndTime_minutes())/60;
                        for(double i = s; i <= e; i++){
                            double finalI = i;
                            if(!booking.isEmpty()){
                                System.out.println("Yes");
                                booking.forEach((k, v)->{
                                    System.out.println(k + " " + v + " " + finalI);
                                    if(k > finalI || finalI >= v){
                                        int h = (int) finalI; int m = (int) ((finalI%1) * 60.0);
                                        int h1 = (int) (finalI+d); int m1 = (int) (((finalI+d)%1)*60.0);
                                        Time t1 = new Time(h,m,0);
                                        Time t2 = new Time(h1,m1,0);

                                        options.add("from " + t1.toString().subSequence(0,5) +
                                                " to " + t2.toString().subSequence(0,5));
                                    }
                                });
                            }
                            else {

                                int h = (int) finalI; int m = (int) ((finalI%1) * 60.0);
                                int h1 = (int) (finalI+d); int m1 = (int) (((finalI+d)%1)*60.0);
                                Time t1 = new Time(h,m,0);
                                Time t2 = new Time(h1,m1,0);

                                options.add("from " + t1.toString().subSequence(0,5) +
                                        " to " + t2.toString().subSequence(0,5));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                /*ArrayList<Appointment> bookingList = bu.BookingList();
                for(Appointment i: bookingList){
                    if(i.getDate().equals(currentDate)){
                        double h = Double.parseDouble((String) i.getStartTime().toString().subSequence(0,2)) + Double.parseDouble((String) i.getStartTime().toString().subSequence(3,5)) / 60.0;
                        double h1 = Double.parseDouble((String) i.getEndTime().toString().subSequence(0,2)) + Double.parseDouble((String) i.getEndTime().toString().subSequence(3,5)) / 60.0;

                        booking.put(h,h1);
                    }
                }
                double d = select.getDuration_hours() + Double.valueOf(select.getDuration_minutes())/60;
                double s = select.getStartTime_hours() + Double.valueOf(select.getStartTime_minutes())/60;
                double e = select.getEndTime_hours() + Double.valueOf(select.getEndTime_minutes())/60;
                for(double i = s; i <= e; i++){
                    double finalI = i;
                    if(!booking.isEmpty()){
                        System.out.println("Yes");
                        booking.forEach((k, v)->{
                            System.out.println(k + " " + v + " " + finalI);
                            if(k > finalI || finalI >= v){
                                int h = (int) finalI; int m = (int) ((finalI%1) * 60.0);
                                int h1 = (int) (finalI+d); int m1 = (int) (((finalI+d)%1)*60.0);
                                Time t1 = new Time(h,m,0);
                                Time t2 = new Time(h1,m1,0);

                                options.add("from " + t1.toString().subSequence(0,5) +
                                        " to " + t2.toString().subSequence(0,5));
                            }
                        });
                    }
                    else {
                        int h = (int) finalI; int m = (int) ((finalI%1) * 60.0);
                        int h1 = (int) (finalI+d); int m1 = (int) (((finalI+d)%1)*60.0);
                        Time t1 = new Time(h,m,0);
                        Time t2 = new Time(h1,m1,0);

                        options.add("from " + t1.toString().subSequence(0,5) +
                                " to " + t2.toString().subSequence(0,5));
                    }
                }*/
            }
        }
       else Toast.makeText(getApplicationContext(), "Select the type of the appointment", Toast.LENGTH_SHORT).show();

       System.out.println(options);
       ArrayAdapter<String > adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
       gridView.setAdapter(adapter);

       gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        findViewById(R.id.btn).setVisibility(View.VISIBLE);
        String s = parent.getItemAtPosition(position).toString();
        //TODO:
        // add a new appointment to the business and to the customer
        Time t1 = new Time(Integer.parseInt(s.substring(5,7)), Integer.parseInt(s.substring(8,10)), 0);
        Time t2 = new Time(Integer.parseInt(s.substring(14,16)), Integer.parseInt(s.substring(17,19)), 0);
        System.out.println(selectDate);
        app = new Appointment(t1,t2,CustomerHomePage.getUser().getName(),
                bu, select.getName(), selectDate);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn){
            //TODO
            // add the app object to the customer and the business owner
            findViewById(R.id.btn).setVisibility(View.INVISIBLE);
        }
    }

    public void setSelectedType(AppointmentType p){
        select = p;
    }
}
