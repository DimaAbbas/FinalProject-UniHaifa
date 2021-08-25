package com.example.finalproject_unihaifa;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DailyBookedAppsAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<String> fullAppName;
    private final ArrayList<String> appointments;
    private final ArrayList<String> customers;
    private final ArrayList<String> phones;
    private final ArrayList<String> hours;
    private final ArrayList<String> minutes;

    public DailyBookedAppsAdapter(Activity context,ArrayList<String> fullAppName, ArrayList<String> appointments,
                                  ArrayList<String> customers, ArrayList<String> phones,
                                  ArrayList<String> hours, ArrayList<String> minutes) {
        super(context, R.layout.list_daily_booked_apps, appointments);
        this.context = context;
        this.fullAppName = fullAppName;
        this.appointments = appointments;
        this.customers = customers;
        this.phones = phones;
        this.hours = hours;
        this.minutes = minutes;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_daily_booked_apps, null, true);

        TextView appTxt = (TextView) rowView.findViewById(R.id.app_type);
        TextView customerTxt = (TextView) rowView.findViewById(R.id.customer_name);
        TextView phoneTxt = (TextView) rowView.findViewById(R.id.customer_phone);
        TextView hourTxt = (TextView) rowView.findViewById(R.id.hours);
        TextView minuteTxt = (TextView) rowView.findViewById(R.id.minutes);
        ImageView delete = (ImageView) rowView.findViewById(R.id.deleteIcon_businessHome);

        appTxt.setText(appointments.get(position));
        customerTxt.setText(customers.get(position));
        phoneTxt.setText(phones.get(position));
        hourTxt.setText(hours.get(position));
        minuteTxt.setText(minutes.get(position));
        delete.setImageResource(R.drawable.ic_baseline_delete_24);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_delete_appointment);
                dialog.setTitle("Delete appointment");

                /*((Button) dialog.findViewById(R.id.delete_appointment)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
                        DatabaseReference userRef1 = FirebaseDatabase.getInstance().getReference("User");
                        DatabaseReference appRef = FirebaseDatabase.getInstance().getReference("Appointments");

                        userRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String business = snapshot.getValue(User.class).getName();
                                appRef.child(business).child(fullAppName.get(position)).removeValue();
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });*/

                ((Button) dialog.findViewById(R.id.cancel_delete_appointment)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(1000, 600);
            }
        });

        return rowView;
    }
}
