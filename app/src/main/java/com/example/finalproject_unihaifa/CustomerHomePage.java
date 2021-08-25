package com.example.finalproject_unihaifa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerHomePage extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView apps;
    TextView username;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, myApp;
    List list = new ArrayList<>();
    ArrayList<Appointment> app = new ArrayList<Appointment>();;
    Map<String, String > item;
    static User user;
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");
        myApp = database.getReference("Appointments");

        apps = (ListView) findViewById(R.id.Appointments);
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.multi_line,
                new String[] {"line1","line2"}, new int[] {R.id.line_a,R.id.line_b});
        apps.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        username = (TextView) findViewById(R.id.username1);

        FirebaseUser current = mAuth.getCurrentUser();

        if(current != null){
            Query query = myRef.child(current.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user_ = snapshot.getValue(User.class);
                    setUser(user_);
                    username.setText(user_.getName() + " ,");
                    myApp = myApp.child(user_.getName());
                    myApp.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for(DataSnapshot i : snapshot.getChildren()){
                                if(i.exists()){
                                    Appointment p = i.getValue(Appointment.class);
                                    int year = Integer.parseInt(p.getDate().substring(6,10))
                                            , month = Integer.parseInt(p.getDate().substring(3,5))
                                            , day = Integer.parseInt(p.getDate().substring(0,2));
                                    String cd = df.format(Calendar.getInstance().getTime());

                                    if(Integer.parseInt(cd.substring(6,10)) > year)
                                        i.getRef().removeValue();

                                    else if(Integer.parseInt(cd.substring(3,5)) > month && Integer.parseInt(cd.substring(6,10)) == year)
                                        i.getRef().removeValue();

                                    else if((Integer.parseInt(cd.substring(0,2)) > day && Integer.parseInt(cd.substring(3,5)) == month && Integer.parseInt(cd.substring(6,10)) == year))
                                        i.getRef().removeValue();

                                    item = new HashMap<String,String>();
                                    item.put("line1", p.getType() + " appointment at " + p.getBusinessN());
                                    item.put("line2", "Time : " + p.getStartTime().toString().subSequence(0,5) + ", Date : " + p.getDate());
                                    list.add(item);
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
        apps.setOnItemClickListener(this);

        findViewById(R.id.B_account).setOnClickListener(this);
        findViewById(R.id.B_new_appointment).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.B_account:
                showPopup(v);
                break;
            case R.id.B_new_appointment:
                startActivity(new Intent(getApplicationContext(), SearchResult.class));
                break;
        }
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.customer_submenu, popup.getMenu());

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.C_account_info:
                        //Toast.makeText(getApplicationContext(), "Your account clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AccountInfo.class));
                        break;

                    case R.id.log_out1:
                        Toast.makeText(getApplicationContext(), "log out clicked", Toast.LENGTH_SHORT).show();
                        Dialog dialog = new Dialog(CustomerHomePage.this);
                        dialog.setContentView(R.layout.dialog_logout);
                        dialog.setTitle("lou out");

                        ((Button) dialog.findViewById(R.id.logout_dialog)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAuth.signOut();
                                dialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        });

                        ((Button) dialog.findViewById(R.id.cancel_logout)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        dialog.getWindow().setLayout(1000, 600);
                        break;
                }

                return true;
            }
        });

    }

    public void setUser(User user){
        this.user = user;
    }
    public static User getUser(){
        return user;
    }
}
