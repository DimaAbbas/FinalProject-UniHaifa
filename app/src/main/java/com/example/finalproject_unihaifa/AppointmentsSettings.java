package com.example.finalproject_unihaifa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentsSettings extends AppCompatActivity implements View.OnClickListener{

    ListView appListView;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, selectedApp;

    List list = new ArrayList<>();
    SimpleAdapter adapter;
    Map<String, String> item;

    String username;

    ArrayList<String> appName = new ArrayList<String>();
    ArrayList<String> appDuration = new ArrayList<String>();
    ArrayList<String> appPrice = new ArrayList<String>();
    ArrayList<Integer> editSymbol = new ArrayList<Integer>();
    AppointmentTypeListAdapter myAdapter;

    AppointmentType appointmentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_settings);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");

        username = LogIn.getUser().getName();
        /*if(mAuth.getCurrentUser() != null){
            myRef = myRef.child(mAuth.getCurrentUser().getUid());
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
        }*/

        findViewById(R.id.addType).setOnClickListener(this);
        appListView = (ListView) findViewById(R.id.appTypesList);

        myAdapter = new AppointmentTypeListAdapter(this, appName, appDuration,
                appPrice, editSymbol);
        appListView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        myRef = database.getReference("Appointment Type");
        Query query = myRef.child(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appName.clear();
                appDuration.clear();
                appPrice.clear();
                editSymbol.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.exists()) {
                        AppointmentType appointmentType = ds.getValue(AppointmentType.class);
                        appName.add(appointmentType.getName());
                        appDuration.add("duration:" + appointmentType.getDuration_hours() +
                                ":" + appointmentType.getDuration_minutes());
                        appPrice.add("price: " + appointmentType.getPrice());
                        editSymbol.add(R.drawable.ic_baseline_edit_24);
                        myAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) appListView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),str, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ViewAppointmentType.class);
                Bundle b = new Bundle();
                b.putString("appName", str);
                intent.putExtras(b);
                startActivity(intent);
            }
        });*/




        /*adapter = new SimpleAdapter(this, list, R.layout.multi_line,
                new String[] {"line1", "line2"}, new int[] {R.id.line_a, R.id.line_b});
        appListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        myRef = database.getReference("Appointment Type");
        Query query = myRef.child(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    if (ds.exists()) {
                        AppointmentType appointmentType = ds.getValue(AppointmentType.class);
                        item = new HashMap<String, String>();
                        item.put("line1", appointmentType.getName());
                        item.put("line2", "duration: " + appointmentType.getDuration_hours() +
                                ":" +appointmentType.getDuration_minutes() +
                                " - " + "price: " + appointmentType.getPrice());
                        //item.put("line3", "price: " + appointmentType.getPrice());
                        list.add(item);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.addType:
                startActivity(new Intent(getApplicationContext(), NewAppointmentType.class));
        }
    }
}
