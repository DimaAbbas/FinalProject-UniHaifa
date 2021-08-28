package com.example.finalproject_unihaifa;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Ref;
import java.util.ArrayList;

public class FeaturedCustomers extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference userRef, customersRef;

    ArrayList<String> customerName = new ArrayList<String>();
    ArrayList<String> customerPhone = new ArrayList<String>();
    ArrayList<String> customerEmail = new ArrayList<String>();
    FeaturedCustomersListAdapter adapter;
    ListView listView;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_customers);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("User");
        customersRef = database.getReference("Featured Customer");

        findViewById(R.id.addFeatureCustomer).setOnClickListener(this);

        listView = findViewById(R.id.featuredCustomersList);
        adapter = new FeaturedCustomersListAdapter(this, customerName, customerPhone, customerEmail);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(mAuth.getCurrentUser() != null){
            userRef = userRef.child(mAuth.getCurrentUser().getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username = user.getName();

                    customersRef.child(username).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            customerName.clear();
                            customerPhone.clear();
                            customerEmail.clear();
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                if (ds.exists()) {
                                    String customer = ds.getValue(String.class);
                                    System.out.println(customer);
                                    Query query = database.getReference("User").orderByChild("name").equalTo(customer);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot ds:snapshot.getChildren()) {
                                                    User user1 = ds.getValue(User.class);
                                                    customerName.add(user1.getName());
                                                    customerPhone.add(user1.getPhone());
                                                    customerEmail.add(user1.getEmail());
                                                    adapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.addFeatureCustomer:
                Toast.makeText(getApplicationContext(), "Add feature customer", Toast.LENGTH_SHORT).show();
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_add_feature_customer);
                dialog.setTitle("add feature customer");

                ((Button) dialog.findViewById(R.id.addCustomer)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("User");

                        EditText featuredCustomerTxt = (EditText) dialog.findViewById(R.id.new_feature_customer_name);
                        String featuredCustomer = featuredCustomerTxt.getText().toString().trim();

                        Query checkUser = ref.orderByChild("name").equalTo(featuredCustomer);
                        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String username = LogIn.getUser().getName();
                                    if (username.equals(featuredCustomer)) {
                                        featuredCustomerTxt.setError("you can't add your self");
                                        featuredCustomerTxt.requestFocus();
                                        return;
                                    }
                                    for (DataSnapshot ds:snapshot.getChildren()) {
                                        if (ds.getValue(User.class).getType().equals("Business Owner")){
                                            featuredCustomerTxt.setError("you can't add a business account as a featured customer");
                                            featuredCustomerTxt.requestFocus();
                                            return;
                                        }
                                    }
                                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Featured Customer");
                                    myRef.child(username).child(featuredCustomer).setValue(featuredCustomer);
                                    Toast.makeText(getApplicationContext(), "featured customer added successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                } else {
                                    featuredCustomerTxt.setError("username does not exists");
                                    featuredCustomerTxt.requestFocus();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                ((ImageButton) dialog.findViewById(R.id.close_new_featured_customer_dialog)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(1000, 600);

                break;
        }

    }
}
