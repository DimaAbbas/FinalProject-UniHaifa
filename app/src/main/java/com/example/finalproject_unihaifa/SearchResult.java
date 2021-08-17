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
import java.util.Map;

public class SearchResult extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    String search;
    EditText searchTxt1;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    List list = new ArrayList<>();
    Map<String,String> item;
    ListView business;
    SimpleAdapter adapter;
    ArrayList<BusinessUser> users = new ArrayList<>();
    static BusinessUser bu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");
        business = (ListView) findViewById(R.id.business);

        adapter = new SimpleAdapter(this, list, R.layout.multi_line,
                new String[] {"line1","line2"}, new int[] {R.id.line_a,R.id.line_b});
        business.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        search = com.example.finalproject_unihaifa.CustomerHomePage.getSearchName();
        setResultList(search);

        searchTxt1 = (EditText) findViewById(R.id.searchTxt1);
        findViewById(R.id.search_btn1).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == (R.id.search_btn1)){
            search = searchTxt1.getText().toString().trim();
            setResultList(search);
        }
    }

    public void setResultList(String text){

        if(search == null || search.length() == 0)
            Toast.makeText(getApplicationContext(), "Please enter a business name", Toast.LENGTH_SHORT).show();
        else {
            list.clear();
            Query check = myRef.orderByChild("name").equalTo(search);
            check.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds: snapshot.getChildren()){
                        BusinessUser user = ds.getValue(BusinessUser.class);
                        if(user.getType().equals("Business Owner")){
                            item = new HashMap<String,String>();
                            item.put("line1", user.getName());
                            item.put("line2", user.getDescription());
                            list.add(item);
                            users.add(user);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            business.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO
        String name = parent.getItemAtPosition(position).toString();
        bu = users.get(position);
        startActivity(new Intent(getApplicationContext(), Booking.class));
    }

    public void setBusinessUser(BusinessUser user){
        bu = user;
    }
    public static BusinessUser getBu(){
        return bu;
    }
}
