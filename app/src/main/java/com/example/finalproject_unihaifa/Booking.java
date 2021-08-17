package com.example.finalproject_unihaifa;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class Booking extends AppCompatActivity {
    Calendar calendar;
    TextView txt;
    ListView list;
    List<AppointmentType> types;
    BusinessUser user;
    String name, newtxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        user = SearchResult.getBu();
        txt = (TextView) findViewById(R.id.textView13);
        name = user.getName();
        newtxt = txt.getText().toString().trim();
        newtxt += name;
        txt.setText(newtxt);

    }
}
