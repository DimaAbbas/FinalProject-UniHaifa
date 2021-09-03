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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.SimpleFormatter;

public class Booking extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    TextView txt, price;
    GridView gridView;
    String name, newtxt;
    Date currentDate;
    String bu, s, cday;
    CalendarView calendar;
    ArrayList<String> typesList = new ArrayList<String>();
    ArrayList<String> options = new ArrayList<String>();
    HashMap<Double,Double> booking = new HashMap<Double,Double>();
    ListPopupWindow statusPopupList;
    AppCompatEditText types;
    AppointmentType select = null;
    Date selectDate = null;
    HashMap<String,Boolean> daysMap = new HashMap<String,Boolean>();
    Appointment app;
    String[] days = { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myApp, myRef, myUser;
    ArrayAdapter adapter;
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myUser = database.getReference("User");
        myApp = database.getReference("Appointments");
        myRef = database.getReference("Appointment Type");

        currentDate = Calendar.getInstance().getTime();
        bu = getIntent().getExtras().getString("businessUser");

        gridView = (GridView) findViewById(R.id.gridView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background));

        types = (AppCompatEditText) findViewById(R.id.types);
        setPopupList();
        setListeners();

        price = (TextView) findViewById(R.id.price);
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
                cday = df.format(currentDate);

                findViewById(R.id.btn).setVisibility(View.INVISIBLE);
                options.clear();
                booking.clear();
                adapter.notifyDataSetChanged();

                if(Integer.parseInt(cday.substring(6,10)) > year)
                    Toast.makeText(getApplicationContext(), "You have selected an old day, select another date", Toast.LENGTH_SHORT).show();

                else if(Integer.parseInt(cday.substring(3,5)) > (month+1) && Integer.parseInt(cday.substring(6,10)) == year)
                    Toast.makeText(getApplicationContext(), "You have selected an old day, select another date", Toast.LENGTH_SHORT).show();

                else if((Integer.parseInt(cday.substring(0,2)) > dayOfMonth && Integer.parseInt(cday.substring(3,5)) == (month+1) && Integer.parseInt(cday.substring(6,10)) == year))
                    Toast.makeText(getApplicationContext(), "You have selected an old day, select another date", Toast.LENGTH_SHORT).show();

                else {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth);
                    int day  = c.get(Calendar.DAY_OF_WEEK);
                    selectDate = new Date(year-1900,month,dayOfMonth);
                    CheckAppointment(df.format(selectDate), days[day-1]);
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
                    ArrayAdapter adapter1 = new ArrayAdapter<String>(Booking.this, R.layout.list_item, R.id.type, typesList);
                    statusPopupList.setAnchorView(types); //this let as set the popup below the EditText
                    statusPopupList.setAdapter(adapter1);
                    statusPopupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            options.clear();
                            adapter.notifyDataSetChanged();
                            findViewById(R.id.btn).setVisibility(View.INVISIBLE);

                            types.setText(parent.getItemAtPosition(position).toString());//we set the selected element in the EditText
                            Query q = myRef.child(bu).child(parent.getItemAtPosition(position).toString());
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(int i = 0; i < 7; i++){
                                            setDaysMap(days[i],
                                                    snapshot.child("days").child(days[i]).getValue(Boolean.class));
                                        }
                                        AppointmentType t = snapshot.getValue(AppointmentType.class);
                                        price.setText("Price : " + t.getPrice());
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
    public void CheckAppointment(String cd, String day){

        if(select != null){
            if(daysMap.get(day) == false || cday.equals(cd)){
                options.clear();
                Toast.makeText(getApplicationContext(), "No such booking received in this day", Toast.LENGTH_SHORT).show();
            }
            else {
                Query query = myApp.orderByChild("businessN").equalTo(bu);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot i : snapshot.getChildren()){
                            if(i.exists()){
                                Appointment p = i.getValue(Appointment.class);
                                if(p.getIsCustomer().equals("true")){
                                    int year = Integer.parseInt(p.getDate().substring(6,10))
                                            , month = Integer.parseInt(p.getDate().substring(3,5))
                                            , day = Integer.parseInt(p.getDate().substring(0,2));

                                    String todayDate = df.format(Calendar.getInstance().getTime());
                                    int year1 = Integer.parseInt(todayDate.substring(6,10));
                                    int month1 = Integer.parseInt(todayDate.substring(3,5));
                                    int day1 = Integer.parseInt(todayDate.substring(0,2));
                                    if (year1 > year)
                                        i.getRef().removeValue();
                                    if(year1 == year && month1 > month)
                                        i.getRef().removeValue();
                                    if (year1 == year && month1 == month && day1 > day)
                                        i.getRef().removeValue();


                                    if(p.getDate().equals(cd)){
                                        double h = Double.parseDouble((String) p.getStartTime().subSequence(0,2)) +
                                                Double.parseDouble((String) p.getStartTime().subSequence(3,5)) / 60.0;
                                        double h1 = Double.parseDouble((String) p.getEndTime().subSequence(0,2)) +
                                                Double.parseDouble((String) p.getEndTime().subSequence(3,5)) / 60.0;

                                        booking.put(h,h1);
                                    }
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
                                            adapter.notifyDataSetChanged();
                                        }
                                        break;
                                    }
                                    else if(!options.contains(str)) {
                                        setOptions(str);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else {
                                setOptions(str);
                                adapter.notifyDataSetChanged();
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
       else Toast.makeText(getApplicationContext(), "Select the type of the appointment", Toast.LENGTH_SHORT).show();

       gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        findViewById(R.id.btn).setVisibility(View.VISIBLE);
        s = parent.getItemAtPosition(position).toString();
        Time t1 = new Time(Integer.parseInt(s.substring(5,7)), Integer.parseInt(s.substring(8,10)), 0);
        Time t2 = new Time(Integer.parseInt(s.substring(14,16)), Integer.parseInt(s.substring(17,19)), 0);

        myUser.orderByChild("name").equalTo(bu).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String phone = ds.getValue(User.class).getPhone();
                    app = new Appointment(t1.toString().substring(0,5),t2.toString().substring(0,5)
                            ,CustomerHomePage.getUser().getName(), bu,
                            select.getName(), df.format(selectDate), CustomerHomePage.getUser().getPhone(), phone, "true");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn){
            findViewById(R.id.btn).setVisibility(View.INVISIBLE);
            options.remove(s);
            adapter.notifyDataSetChanged();
            String str_ = app.getDate() + " " + app.getStartTime() + "-" + app.getEndTime()
                    + " " + app.getBusinessN() + " " + app.getType()
                    + " " + app.getCustomerN();
            myApp.child(str_).setValue(app);
        }
    }

    public void setSelectedType(AppointmentType p){
        select = p;
    }

    public void setDaysMap(String day, Boolean tf){
        daysMap.put(day,tf);
    }

    public void setOptions(String s){
        options.add(s);
    }
}
