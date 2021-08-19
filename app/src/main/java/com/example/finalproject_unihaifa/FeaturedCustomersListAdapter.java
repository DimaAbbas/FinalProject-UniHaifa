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

public class FeaturedCustomersListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<String> name;
    private final ArrayList<String> phone;
    private final ArrayList<String> email;

    public FeaturedCustomersListAdapter(Activity context, ArrayList<String> name, ArrayList<String> phone,
                                        ArrayList<String> email) {
        super(context, R.layout.featured_customers_list, name);
        this.context = context;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.featured_customers_list, null, true);

        TextView nameTxt = (TextView) rowView.findViewById(R.id.customersName);
        TextView phoneTxt = (TextView) rowView.findViewById(R.id.customersPhone);
        TextView emailTxt = (TextView) rowView.findViewById(R.id.customersEmail);
        ImageView delete = (ImageView) rowView.findViewById(R.id.deleteFeaturedCustomer);

        nameTxt.setText(name.get(position));
        phoneTxt.setText(phone.get(position));
        emailTxt.setText(email.get(position));
        delete.setImageResource(R.drawable.ic_baseline_delete_24);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_remove_featured_customer);
                dialog.setTitle("Remove featured customer");

                ((Button) dialog.findViewById(R.id.remove_customer_dialog)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("User");
                        ref = ref.child(mAuth.getCurrentUser().getUid());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String username = snapshot.getValue(User.class).getName();
                                FirebaseDatabase mydatabase = FirebaseDatabase.getInstance();
                                DatabaseReference myref = mydatabase.getReference("Featured Customer");
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

                ((Button) dialog.findViewById(R.id.cancel_remove_customer_dialog)).setOnClickListener(new View.OnClickListener() {
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
