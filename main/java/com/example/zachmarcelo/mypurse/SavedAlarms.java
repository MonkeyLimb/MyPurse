package com.example.zachmarcelo.mypurse;

/**
 * Created by Zach Marcelo on 12/10/2018.
 */

public class SavedAlarms {
    public String hour;
    public String minute;
    public String timeinmilli;
    public String reminder;


    public String key;
    public Boolean repeat, active;
    public SavedAlarms() {
        
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SavedAlarms(String hour, String minute, String timeinmilli, String reminder, Boolean repeat, Boolean active) {
        this.hour =  hour;
        this.minute = minute;
        this.timeinmilli = timeinmilli;
        this.reminder = reminder;
        this.repeat =   repeat;
        this.active     =   active;


    }

    public String getTimeinmilli() {
        return timeinmilli;
    }

    public void setTimeinmilli(String timeinmilli) {
        this.timeinmilli = timeinmilli;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public String gethour() {
        return hour;
    }

    public void sethour(String hour) {
        this.hour = hour;
    }

    public String getminute() {
        return minute;
    }

    public void setminute(String minute) {
        this.minute = minute;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
