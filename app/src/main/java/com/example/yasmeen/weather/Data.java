package com.example.yasmeen.weather;

/**
 * Created by yasmeen on 11/18/2016.
 */
public class Data {

    private String min , max , day , icon , desc ;


    public Data(String min, String max, String icon, String day , String desc) {
        this.min = min;
        this.max = max;
        this.icon = icon;
        this.day = day;
        this.desc = desc ;

    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public String getDay() {
        return day;
    }

    public String getIcon() {
        return icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
