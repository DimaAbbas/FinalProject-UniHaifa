package com.example.finalproject_unihaifa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchResult extends AppCompatActivity implements View.OnClickListener {
    String search;
    EditText searchTxt1;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, myApp;
    ListView business;
    static BusinessUser bu;
    Boolean tf = false;

    SearchResultListAdapter adapter;
    ArrayList<String> businessUsernames = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");
        myApp = database.getReference("Appointment Type");
        business = (ListView) findViewById(R.id.business);

        adapter = new SearchResultListAdapter(this, businessUsernames);
        business.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        businessUsernames.clear();
        myRef.orderByChild("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        BusinessUser user = ds.getValue(BusinessUser.class);
                        if (user.getType().equals("Business Owner")) {
                            businessUsernames.add(user.getName());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "There are no business owners in the app yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchTxt1 = (EditText) findViewById(R.id.searchTxt1);
        findViewById(R.id.search_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == (R.id.search_btn)) {
            search = searchTxt1.getText().toString().trim();
            setResultList(search);
        }
    }

    public void setResultList(String text) {

        /*if(search == null || search.length() == 0){
            //Toast.makeText(getApplicationContext(), "Please enter a business name", Toast.LENGTH_SHORT).show();
            businessUsernames.clear();
            myRef.orderByChild("type").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot ds : snapshot.getChildren()){
                            BusinessUser user = ds.getValue(BusinessUser.class);
                            if(user.getType().equals("Business Owner")){
                                businessUsernames.add(user.getName());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "There are no business owners in the app yet.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }*/
        tf = false;
        businessUsernames.clear();
        myRef.orderByChild("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        BusinessUser user = ds.getValue(BusinessUser.class);
                        if (user.getType().equals("Business Owner")) {
                            if (user.getName().toLowerCase().contains(search.toLowerCase()) ||
                                    user.getDescription().toLowerCase().contains(search.toLowerCase()) ||
                                    search == null || search.length() == 0) {
                                businessUsernames.add(user.getName());
                                adapter.notifyDataSetChanged();
                                tf = true;
                            }
                            else{
                                myApp.child(user.getName()).orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            for(DataSnapshot ds: snapshot.getChildren()){
                                                AppointmentType app = ds.getValue(AppointmentType.class);
                                                if(app.getName().toLowerCase().contains(search.toLowerCase())){
                                                    businessUsernames.add(user.getName());
                                                    adapter.notifyDataSetChanged();
                                                    tf = true;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                    if(!tf)
                        Toast.makeText(getApplicationContext(), "Insert another name or description", Toast.LENGTH_SHORT).show();
                }
                else{
                    businessUsernames.clear();
                    Toast.makeText(getApplicationContext(), "There are no business owners in the app yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //business.setOnItemClickListener(this);
    }


    public void setBusinessUser(BusinessUser user){
        bu = user;
    }
    public static BusinessUser getBu(){
        return bu;
    }
}
