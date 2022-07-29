package com.example.fyplatest1;

public class Activities {

    String title, pace, time;
    Integer num, day, month,year;
    Double distance;

    public Activities() {}

    public Activities(Integer num,String title,Double distance, String time, String pace, Integer day, Integer month, Integer year) {
        this.num = num;
        this.title = title;
        this.distance = distance;
        this.time = time;
        this.pace = pace;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
