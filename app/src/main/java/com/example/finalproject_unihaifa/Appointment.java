package com.example.finalproject_unihaifa;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment {
    private Time startTime, endTime;
    private String customerN, businessN, Type;
    private Date date;

    public Appointment(){

    }

    public Appointment(Time startTime, Time endTime, String customerN, String businessN, String type, Date date){
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Time getEndTime() {
        return endTime;
    }

    public String getBusinessN() {
        return businessN;
    }

    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String s_date = sdf.format(date);
        return s_date;
    }
}
