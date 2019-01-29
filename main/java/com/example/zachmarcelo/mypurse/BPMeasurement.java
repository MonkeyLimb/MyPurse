package com.example.zachmarcelo.mypurse;

/**
 * Created by Zach Marcelo on 11/20/2018.
 */

public class BPMeasurement {

    public String systolic, diastolic, pulse_pressure, timeinmilli;



    public String key;

    public BPMeasurement(){

    }

    public BPMeasurement(String usersys, String userdia, String userpp, String timeinmilli) {
        this.systolic = usersys;
        this.diastolic = userdia;
        this.pulse_pressure = userpp;
        this.timeinmilli = timeinmilli;

    }

    public String getsystolic() {
        return systolic;
    }

    public void setsystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getdiastolic() {
        return diastolic;
    }

    public void setdiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public String getPulse_pressure() {
        return pulse_pressure;
    }

    public void setPulse_pressure(String pulse_pressure) {
        this.pulse_pressure = pulse_pressure;
    }

    public String getTimeinmilli() {
        return timeinmilli;
    }

    public void setTimeinmilli(String timeinmilli) {
        this.timeinmilli = timeinmilli;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
