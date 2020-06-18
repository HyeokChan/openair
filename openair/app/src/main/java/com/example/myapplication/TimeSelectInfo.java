package com.example.myapplication;

public class TimeSelectInfo {
    public String time;
    public String reserve;
    public String dust;
    public boolean isSelected;

    public TimeSelectInfo(String time, String reserve, String dust, boolean isSelected){
        this.time = time;
        this.reserve = reserve;
        this.dust = dust;
        this.isSelected = isSelected;
    }

    public String getTime(){
        return time;
    }
    public String getReserve()
    {
        return reserve;
    }
    public String getDust() {
        return dust;
    }
    public boolean isSelected()
    {
        return isSelected;
    }
}