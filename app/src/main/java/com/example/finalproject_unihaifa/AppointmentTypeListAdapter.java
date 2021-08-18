package com.example.finalproject_unihaifa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppointmentTypeListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<String> name;
    private final ArrayList<String> duration;
    private final ArrayList<String> price;
    private final ArrayList<Integer>  symbol;

    public AppointmentTypeListAdapter(Activity context, ArrayList name, ArrayList duration,
                                      ArrayList price, ArrayList symbol) {
        super(context, R.layout.appointment_type_list, name);
        this.context = context;
        this.name = name;
        this.duration = duration;
        this.price = price;
        this.symbol = symbol;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.appointment_type_list, null, true);

        TextView nameTxt = (TextView) rowView.findViewById(R.id.appTypeName);
        TextView durationTxt = (TextView) rowView.findViewById(R.id.appTypeDuration);
        TextView priceTxt = (TextView) rowView.findViewById(R.id.appTypePrice);
        ImageView edit = (ImageView) rowView.findViewById(R.id.editIcon);
        ImageView delete = (ImageView) rowView.findViewById(R.id.deleteIcon);

        nameTxt.setText(name.get(position));
        durationTxt.setText(duration.get(position));
        priceTxt.setText(price.get(position));
        edit.setImageResource(R.drawable.ic_baseline_edit_24);
        delete.setImageResource(R.drawable.ic_baseline_delete_24);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewAppointmentType.class);
                Bundle b = new Bundle();
                b.putString("appName", name.get(position));
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.delete_app_type_dialog);
                dialog.setTitle("Delete appointment type");

                ((Button) dialog.findViewById(R.id.delete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "delete clicked", Toast.LENGTH_SHORT).show();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("User");
                        ref = ref.child(mAuth.getCurrentUser().getUid());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String username = snapshot.getValue(User.class).getName();
                                FirebaseDatabase mydatabase = FirebaseDatabase.getInstance();
                                DatabaseReference myref = mydatabase.getReference("Appointment Type");
                                myref = myref.child(username).child(name.get(position));
                                myref.removeValue();
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                
                ((Button) dialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
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
