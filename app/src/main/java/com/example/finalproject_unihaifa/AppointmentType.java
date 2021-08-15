package com.example.finalproject_unihaifa;

public class AppointmentType {
    private String TypeName;
    private int duration;
    private boolean[] days = new boolean[7];
    private int startTime;
    private int endTime;
    private int price;

    public AppointmentType(){

    }

    public AppointmentType(String name, int duration, int startTime, int endTime, int price){
        this.TypeName = name;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public AppointmentType(String name, int duration, boolean[] days, int startTime, int endTime, int price){
        this.TypeName = name;
        this.duration = duration;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public String getName() {
        return this.TypeName;
    }

    public int getDuration  (){
        return this.duration;
    }

    public boolean[] getDays() {
        return days;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getPrice() {
        return price;
    }
}
