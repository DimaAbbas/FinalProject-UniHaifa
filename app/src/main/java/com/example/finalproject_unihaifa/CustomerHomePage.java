package com.example.finalproject_unihaifa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerHomePage extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView apps;
    EditText searchTxt;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    List list = new ArrayList<>();
    ArrayList<Appointment> app = new ArrayList<Appointment>();;
    Map<String, String > item;
    static String search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");
        apps = (ListView) findViewById(R.id.Appointments);
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.multi_line,
                new String[] {"line1","line2"}, new int[] {R.id.line_a,R.id.line_b});
        apps.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FirebaseUser current = mAuth.getCurrentUser();

        if(current != null){
            myRef = myRef.child(current.getUid());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    app = user.getAppointments();
                    for(Appointment i : app){
                        item = new HashMap<String,String>();
                        item.put("line1", i.getType() + " appointment at " + i.getBusinessN());
                        item.put("line2", "Time : " + i.getStartTime().toString().subSequence(0,5) + ", Date : " + i.getDate());
                        list.add(item);
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        apps.setOnItemClickListener(this);

        searchTxt = (EditText) findViewById(R.id.searchTxt);
        findViewById(R.id.C_account).setOnClickListener(this);
        findViewById(R.id.search_btn).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.C_account:
                startActivity(new Intent(getApplicationContext(), AccountInfo.class));
                break;
            case R.id.search_btn:
                setSearchName(searchTxt.getText().toString().trim());
                startActivity(new Intent(getApplicationContext(), com.example.finalproject_unihaifa.SearchResult.class));
                break;
        }

    }

    public void setSearchName(String searchTxt){
        search = searchTxt;
    }
    public static String getSearchName(){
        return search;
    }
}
