package com.example.finalproject_unihaifa;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment {
    private String startTime, endTime;
    private String customerN, businessN, type;
    private String date;
    private String customerPhone;
    private String businessPhone;
    private String isCustomer;

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

    public Appointment(String startTime, String endTime, String customerN, String businessN, String type, String date, String customerPhone, String businessPhone, String isCustomer){
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerN = customerN;
        this.businessN = businessN;
        this.type = type;
        this.date = date;
        this.customerPhone = customerPhone;
        this.businessPhone = businessPhone;
        this.isCustomer = isCustomer;
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

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public String getIsCustomer(){
        return isCustomer;
    }
}
