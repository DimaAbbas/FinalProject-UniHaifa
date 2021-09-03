package com.example.finalproject_unihaifa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<String> businessUsername;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRequests = database.getReference("Customer Requests");

    public SearchResultListAdapter(Activity context, ArrayList usernames) {

        super(context, R.layout.list_search_result, usernames);
        this.context = context;
        this.businessUsername = usernames;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_search_result, null, true);

        TextView usernameTxt = (TextView) rowView.findViewById(R.id.BusinessUsername_searchResult);
        ImageView info = (ImageView) rowView.findViewById(R.id.BusinessInfoIcon);
        ImageView book = (ImageView) rowView.findViewById(R.id.BookAppointmentIcon);

        usernameTxt.setText(businessUsername.get(position));
        info.setImageResource(R.drawable.ic_baseline_info_24);
        book.setImageResource(R.drawable.ic_baseline_book_online_24);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_business_info);
                dialog.setTitle("Business info");

                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("User");
                Query query = myRef.orderByChild("name").equalTo(businessUsername.get(position));
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()) {
                            BusinessUser user = ds.getValue(BusinessUser.class);
                            ((TextView)dialog.findViewById(R.id.business_name_dialog)).setText(user.getName());
                            ((TextView)dialog.findViewById(R.id.business_phone_dialog)).setText(user.getPhone());
                            ((TextView)dialog.findViewById(R.id.business_description_dialog)).setText(user.getDescription());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                ((Button) dialog.findViewById(R.id.close_business_dialog)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(1000,600);

            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User");
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Featured Customer");
                DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Appointment Type");

                ref1.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String currentUser = snapshot.getValue(User.class).getName();
                        ref3.child(usernameTxt.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                System.out.println(snapshot);
                                if(snapshot.getChildrenCount() > 0){
                                    ref2.child(businessUsername.get(position)).child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Intent intent = new Intent(context, Booking.class);
                                                Bundle b = new Bundle();
                                                b.putString("businessUser", businessUsername.get(position));
                                                intent.putExtras(b);
                                                context.startActivity(intent);
                                            }
                                            else {
                                                Dialog dialog = new Dialog(context);
                                                dialog.setContentView(R.layout.dialog_contact_business);
                                                dialog.setTitle("contact business");

                                                Query check = myRequests.child(businessUsername.get(position)).child(currentUser);
                                                check.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()){
                                                            ((Button)dialog.findViewById(R.id.customer_request_button)).setClickable(false);
                                                            ((Button)dialog.findViewById(R.id.customer_request_button)).setVisibility(View.INVISIBLE);
                                                            ((Button)dialog.findViewById(R.id.request_sent_button)).setVisibility(View.VISIBLE);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                                ((ImageButton)dialog.findViewById(R.id.close_contact_business_dialog))
                                                        .setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                ((Button)dialog.findViewById(R.id.customer_request_button)).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Query check = myRequests.child(businessUsername.get(position)).child(currentUser);
                                                        check.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (!snapshot.exists()){
                                                                    myRequests.child(businessUsername.get(position)).child(currentUser).setValue(currentUser);
                                                                    Toast.makeText(context.getApplicationContext(), "a customer request was sent", Toast.LENGTH_SHORT).show();
                                                                    dialog.dismiss();
                                                                }
                                                                /*if(snapshot.exists()){
                                                                    dialog.dismiss();
                                                                    Toast.makeText(context.getApplicationContext(),"You have send customer request", Toast.LENGTH_LONG).show();
                                                                }
                                                                else {
                                                                    myRequests.child(businessUsername.get(position)).child(currentUser).setValue(currentUser);
                                                                    dialog.dismiss();
                                                                }*/
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                });

                                                dialog.show();
                                                dialog.getWindow().setLayout(1000,700);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(context.getApplicationContext(), "This business owner doesn't have an appointment types", Toast.LENGTH_SHORT).show();
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

        return rowView;
    }
}
