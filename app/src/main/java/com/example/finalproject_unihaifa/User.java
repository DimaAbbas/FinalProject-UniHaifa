package com.example.finalproject_unihaifa;

import java.util.List;

public class User {
    private String name, email, phone, password;
    private String type; // Customer or Business Owner
    private List<Appointment> appointments; //The type can be change according to usage

    public User(){

    }

    public User(String name, String phone, String email, String password, String type){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public User(User user) {
        this.name = user.name;
        this.phone = user.phone;
        this.email = user.email;
        this.password = user.password;
        this.type = user.type;
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

    public List<Appointment> getAppointments() {
        return this.appointments;
    }

}

