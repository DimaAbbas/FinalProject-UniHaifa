package com.example.finalproject_unihaifa;

import android.text.format.Time;

public class Appointment {
    private Time startTime, finishTime;
    private String customerN, businessN, Type;

    public Appointment(){

    }

    public Appointment(Time startTime, Time finishTime, String customerN, String businessN, String type){
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.customerN = customerN;
        this.businessN = businessN;
        this.Type = type;
    }
}
