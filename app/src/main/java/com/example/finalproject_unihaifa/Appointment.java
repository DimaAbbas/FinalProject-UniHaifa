package com.example.finalproject_unihaifa;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment {
    private Time startTime, finishTime;
    private String customerN, businessN, Type;
    private Date date;

    public Appointment(){

    }

    public Appointment(Time startTime, Time finishTime, String customerN, String businessN, String type, Date date){
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.customerN = customerN;
        this.businessN = businessN;
        this.Type = type;
        this.date = date;
    }
    public String getType() {
        return Type;
    }

    public Time getStartTime() {
        return startTime;
    }

    public String getBusinessN() {
        return businessN;
    }

    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        String s_date = sdf.format(date);
        return s_date;
    }
}
