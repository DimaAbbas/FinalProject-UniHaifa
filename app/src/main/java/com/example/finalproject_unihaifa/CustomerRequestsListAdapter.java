package com.example.finalproject_unihaifa;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerRequestsListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<String> requests;
    String name_;
    DatabaseReference myRef, myReq, myFeature;
    FirebaseAuth mAuth;

    public CustomerRequestsListAdapter(Activity context, ArrayList requests) {

        super(context, R.layout.list_request, requests);
        this.context = context;
        this.requests = requests;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_request, null, true);

        TextView c_name = (TextView) rowView.findViewById(R.id.cr_name);
        ImageView accept = (ImageView) rowView.findViewById(R.id.accept);
        ImageView delete = (ImageView) rowView.findViewById(R.id.delete1);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("User");
        myReq = FirebaseDatabase.getInstance().getReference("Customer Requests");
        myFeature = FirebaseDatabase.getInstance().getReference("Featured Customer");

        String uid = mAuth.getCurrentUser().getUid();

        c_name.setText(requests.get(position));
        name_ = c_name.getText().toString().trim();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue(BusinessUser.class).getName();
                        myReq.child(name).child(name_).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    myFeature.child(name).child(name_).setValue(name_);
                                    requests.remove(position);
                                    notifyDataSetChanged();
                                    snapshot.getRef().removeValue();
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
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue(BusinessUser.class).getName();
                        myReq.child(name).child(name_).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeValue();
                                myFeature.child(name).child(name_).getRef().removeValue();
                                requests.remove(position);
                                notifyDataSetChanged();
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
        });

        return rowView;
    }
}
