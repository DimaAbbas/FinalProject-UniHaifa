package com.example.finalproject_unihaifa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BusinessHomePage extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    String userName;
    TextView userNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home);
        mAuth = FirebaseAuth.getInstance();

        //getting the user's name to show on top of screen
        userName = LogIn.getUser().getName() + " ,";
        userNameView = (TextView) findViewById(R.id.BusinessUserNameText);
        userNameView.setText(userName);

        findViewById(R.id.B_account).setOnClickListener(this);
        findViewById(R.id.B_new_appointment).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.B_account){
            Toast.makeText(getApplicationContext(), "Account button clicked", Toast.LENGTH_LONG).show();
            showPopup(view);
        }
        else if (view.getId() == R.id.B_new_appointment){
            Toast.makeText(getApplicationContext(), "New Appointment button clicked", Toast.LENGTH_LONG).show();
        }
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.business_submenu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.B_account_info:
                        //Toast.makeText(getApplicationContext(), "Your account clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AccountInfo.class));
                        break;
                    case R.id.B_Appointments:
                        //Toast.makeText(getApplicationContext(), "appointments settings clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AppointmentsSettings.class));
                        break;
                    case R.id.customers_list:
                        //Toast.makeText(getApplicationContext(), "customer list clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), FeaturedCustomers.class));
                        break;

                    case R.id.log_out:{
                        Toast.makeText(getApplicationContext(), "log out clicked", Toast.LENGTH_SHORT).show();
                        Dialog dialog = new Dialog(BusinessHomePage.this);
                        dialog.setContentView(R.layout.dialog_logout);
                        dialog.setTitle("lou out");

                        ((Button) dialog.findViewById(R.id.logout_dialog)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
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

                    default:
                        break;
                }

                return true;
            }
        });

    }
}
