package com.example.finalproject_unihaifa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerBookedAppsListAdapter extends ArrayAdapter {

    private Activity context;
    private ArrayList booked;
    private String username;
    HashMap<String,String> item = new HashMap<>();

    public CustomerBookedAppsListAdapter(Activity context, ArrayList booked, String username) {
        super(context, R.layout.multi_line_1, booked);
        this.booked = booked;
        this.context = context;
        this.username = username;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.multi_line_1, null, true);

        TextView line1 = (TextView) rowView.findViewById(R.id.line_a_1);
        TextView line2 = (TextView) rowView.findViewById(R.id.line_b_1);
        ImageView delete = (ImageView) rowView.findViewById(R.id.delete_app);

        item = (HashMap<String, String>) booked.get(position);
        line1.setText(item.get("line1"));
        line2.setText(item.get("line2"));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_delete_appointment);
                dialog.setTitle("Delete appointment");

                ((Button) dialog.findViewById(R.id.delete_appointment)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference appRef = FirebaseDatabase.getInstance().getReference("Appointments");
                        String time = item.get("line2").substring(7,18), date = item.get("line2").substring(27,37);
                        String[] split = item.get("line1").split(" appointment at ");
                        String firstSubString = split[0], secondSubString = split[1];
                        String str = date + " " + time + " " + secondSubString
                                + " " + firstSubString + " " + username;
                        appRef.child(str).removeValue();
                        booked.remove(position);
                        //notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

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
