package com.example.finalproject_unihaifa;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment {
    private String startTime, endTime;
    private String customerN, businessN, type;
    private String date;

    public Appointment(){

    }

    public Appointment(String startTime, String endTime, String customerN, String businessN, String type, String date){
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerN = customerN;
        this.businessN = businessN;
        this.type = type;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getBusinessN() {
        return businessN;
    }

    public String getDate(){
        return date;
    }

    public String getCustomerN() {
        return customerN;
    }
}
