package com.example.finalproject_unihaifa;

import java.util.Map;

public class AppointmentType {
    private String name;
    private Map<String, Boolean> days;
    private int price;
    private int duration_hours;
    private int duration_minutes;
    private int startTime_hours;
    private int startTime_minutes;
    private int endTime_hours;
    private int endTime_minutes;


    public AppointmentType(){

    }

    public AppointmentType(String name, Map<String, Boolean> days, int price, int durationH, int durationM, int startTimeH,
                           int startTimeM, int endTimeH, int endTimeM){
        this.name = name;
        this.days = days;
        this.price = price;
        this.duration_hours = durationH;
        this.duration_minutes = durationM;
        this.startTime_hours = startTimeH;
        this.startTime_minutes = startTimeM;
        this.endTime_hours = endTimeH;
        this.endTime_minutes = endTimeM;
    }

    public String getName() {
        return this.name;
    }

    public int getDuration_hours() {
        return duration_hours;
    }

    public int getDuration_minutes() {
        return duration_minutes;
    }

    public int getStartTime_hours() {
        return startTime_hours;
    }

    public int getStartTime_minutes() {
        return startTime_minutes;
    }

    public int getEndTime_hours() {
        return endTime_hours;
    }

    public int getEndTime_minutes() {
        return endTime_minutes;
    }

    public int getPrice() {
        return price;
    }
}
