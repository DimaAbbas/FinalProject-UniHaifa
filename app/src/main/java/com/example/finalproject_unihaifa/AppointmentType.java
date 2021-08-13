package com.example.finalproject_unihaifa;

public class AppointmentType {
    private String Type, length_time; //Not sure about the type of length_time

    public AppointmentType(){

    }

    public AppointmentType(String Type, String length){
        this.Type = Type;
        this.length_time = length;
    }

    public String getLength_time(){
        return this.length_time;
    }
}
