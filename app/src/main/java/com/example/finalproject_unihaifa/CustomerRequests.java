package com.example.finalproject_unihaifa;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerRequests extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myReq, myRef;
    ArrayList<String> requests = new ArrayList<>();
    CustomerRequestsListAdapter adapter;
    ListView C_Requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_requests);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myReq = database.getReference("Customer Requests");
        myRef = database.getReference("User");

        C_Requests = (ListView) findViewById(R.id.cr_list);

        adapter = new CustomerRequestsListAdapter(this, requests);
        C_Requests.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FirebaseUser current = mAuth.getCurrentUser();

        if(current != null){
            Query q = myRef.child(current.getUid());
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    BusinessUser bu = snapshot.getValue(BusinessUser.class);
                    Query q1 = myReq.child(bu.getName());
                    q1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            requests.clear();
                            for(DataSnapshot i : snapshot.getChildren()){
                                if(i.exists()){
                                    String name = i.getValue(String.class);
                                    requests.add(name);
                                    adapter.notifyDataSetChanged();
                                }
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
        }

    }
}
