package com.example.finalproject_unihaifa;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String name, email, phone, password;
    private String type; // Customer or Business Owner
    private ArrayList<Appointment> appointments;//The type can be change according to usage
    //private ArrayList<User> business;
    public User(){

    }

    public User(String name, String phone, String email, String password, String type){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.type = type;
        this.appointments = new ArrayList<Appointment>();
    }

    public User(User user) {
        this.name = user.name;
        this.phone = user.phone;
        this.email = user.email;
        this.password = user.password;
        this.type = user.type;
        this.appointments = user.appointments;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getPhone(){
        return this.phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return this.password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }

    public void addAppointment(Appointment app){
        this.appointments.add(app);
    }
    public void deleteAppointment(Appointment app){
        this.appointments.remove(app);
    }

    public ArrayList<Appointment> getAppointments() {
        appointments = new ArrayList<>();
        Time t1 = new Time(10,0,0);
        Time t2 = new Time(13,0,0);
        Date date = new Date(2021-1900,8,17);
        Appointment app = new Appointment(t1,t2,name,"Suzan Kassabry","lak gel", date);
        appointments.add(app);
        return this.appointments;
    }

}

