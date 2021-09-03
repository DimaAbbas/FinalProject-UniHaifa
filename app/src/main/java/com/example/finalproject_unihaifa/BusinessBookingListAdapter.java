package com.example.finalproject_unihaifa;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;

public class BusinessBookingListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<String> availableApps;

    public BusinessBookingListAdapter(Activity context, ArrayList options) {

        super(context, R.layout.list_search_result, options);
        this.context = context;
        this.availableApps = options;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_business_booking, null, true);

        TextView availableTxt = (TextView) rowView.findViewById(R.id.available_apps);
        Button book = (Button) rowView.findViewById(R.id.book_business_list);

        availableTxt.setText(availableApps.get(position));

        final Boolean[] registered = {false};

        String s = availableApps.get(position);
        Time t1 = new Time(Integer.parseInt(s.substring(5,7)), Integer.parseInt(s.substring(8,10)), 0);
        Time t2 = new Time(Integer.parseInt(s.substring(14,16)), Integer.parseInt(s.substring(17,19)), 0);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_business_booking);
                dialog.setTitle("book appointment");

                Switch mySwitch = dialog.findViewById(R.id.business_booking_switch);
                EditText nameTxt = dialog.findViewById(R.id.name);
                EditText phoneTxt = dialog.findViewById(R.id.phone);
                EditText usernameTxt = dialog.findViewById(R.id.username);

                mySwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mySwitch.isChecked()) {
                            nameTxt.setVisibility(View.INVISIBLE);
                            phoneTxt.setVisibility(View.INVISIBLE);
                            usernameTxt.setVisibility(View.VISIBLE);
                            mySwitch.setText("Yes");
                        } else {
                            nameTxt.setVisibility(View.VISIBLE);
                            phoneTxt.setVisibility(View.VISIBLE);
                            usernameTxt.setVisibility(View.INVISIBLE);
                            mySwitch.setText("No");

                        }
                    }
                });

                ((FloatingActionButton) dialog.findViewById(R.id.book_floating_action))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                User bu_ = snapshot.getValue(User.class);
                                                String bu = bu_.getName();
                                                String bPhone = bu_.getPhone();

                                                if (mySwitch.isChecked()) {
                                                    String customerUsername = usernameTxt.getText().toString().trim();
                                                    if (customerUsername.isEmpty()) {
                                                        usernameTxt.setError("Enter username");
                                                        usernameTxt.requestFocus();
                                                        return;
                                                    }
                                                    ref.orderByChild("name").equalTo(customerUsername)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (!snapshot.exists()) {
                                                                usernameTxt.setError("this username does not exist");
                                                                usernameTxt.requestFocus();
                                                                return;
                                                            } else {
                                                                for (DataSnapshot ds: snapshot.getChildren()) {
                                                                    if ((ds.getValue(User.class).getType()).equals("Business Owner")) {
                                                                        usernameTxt.setError("this username is a Business Owner user, " +
                                                                                "you can't book them an appointment");
                                                                        usernameTxt.requestFocus();
                                                                        return;
                                                                    }
                                                                    String phone = ds.getValue(User.class).getPhone();
                                                                    Appointment appointment = new Appointment(t1.toString().substring(0,5),
                                                                            t2.toString().substring(0,5), customerUsername, bu,
                                                                            BusinessBooking.getSelectedType(), BusinessBooking.getDate(), phone, bPhone, "true");
                                                                    String str = appointment.getDate() + " " + appointment.getStartTime() + "-" + appointment.getEndTime()
                                                                            + " " + appointment.getBusinessN() + " " + appointment.getType()
                                                                            + " " + appointment.getCustomerN();
                                                                    DatabaseReference myApp = FirebaseDatabase.getInstance().getReference("Appointments");
                                                                    myApp.child(str).setValue(appointment);
                                                                }
                                                                //String str = appointment.getStartTime() + " - " + appointment.getEndTime() + ", " + appointment.getDate();
                                                                //myApp.child(bu).child(str).setValue(appointment);
                                                                //myApp.child(appointment.getCustomerN()).child(str).setValue(appointment);
                                                                //availableApps.remove(s);
                                                                notifyDataSetChanged();
                                                                dialog.dismiss();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                } else {
                                                    String customerName = nameTxt.getText().toString().trim();
                                                    String customerPhone = phoneTxt.getText().toString().trim();

                                                    if (customerName.isEmpty()) {
                                                        nameTxt.setError("Enter customer's name");
                                                        nameTxt.requestFocus();
                                                        return;
                                                    }
                                                    if (customerPhone.isEmpty()) {
                                                        phoneTxt.setError("Enter customer's phone");
                                                        phoneTxt.requestFocus();
                                                        return;
                                                    }
                                                    if (customerPhone.length() != 10) {
                                                        phoneTxt.setError("Enter valid phone number");
                                                        phoneTxt.requestFocus();
                                                        return;
                                                    }

                                                    Appointment appointment = new Appointment(t1.toString().substring(0,5),
                                                            t2.toString().substring(0,5), customerName, bu,
                                                            BusinessBooking.getSelectedType(), BusinessBooking.getDate(), customerPhone, bPhone, "false");

                                                    //String str = appointment.getStartTime() + " - " + appointment.getEndTime() + ", " + appointment.getDate();
                                                    String str = appointment.getDate() + " " + appointment.getStartTime() + "-" + appointment.getEndTime()
                                                            + " " + appointment.getBusinessN() + " " + appointment.getType()
                                                            + " " + appointment.getCustomerN();
                                                    DatabaseReference myApp = FirebaseDatabase.getInstance().getReference("Appointments");
                                                    myApp.child(str).setValue(appointment);
                                                    //myApp.child(bu).child(str).setValue(appointment);
                                                    //availableApps.remove(s);
                                                    notifyDataSetChanged();
                                                    dialog.dismiss();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        }
                                );
                            }
                        });

                dialog.show();
                dialog.getWindow().setLayout(1000, 900);
            }
        });

        return rowView;
    }
}
