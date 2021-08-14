package com.example.finalproject_unihaifa;

import java.util.List;

public class BusinessUser extends User {

    String description;
    int appointmentsTypesCount;
    List<AppointmentType> appointmentTypes;

    public BusinessUser (String name, String phone, String email, String password, String type,
                         String description) {
        super(name, phone, email, password, type);
        this.description = description;
        appointmentsTypesCount = 0;
        appointmentTypes = null;
    }

    public BusinessUser (BusinessUser user) {
        super(user);
        this.description = user.description;
        this.appointmentsTypesCount = user.appointmentsTypesCount;
        this.appointmentTypes = user.appointmentTypes;
    }

    public void addAppointmentType(AppointmentType newAppointmentType) {
        appointmentTypes.add(newAppointmentType);
        appointmentsTypesCount += 1;
    }

    public String getDescription() {return this.description;}
    public int getAppointmentsTypesCount() {return this.appointmentsTypesCount;}
    public List<AppointmentType> getAppointmentTypes() {return this.appointmentTypes;}
}
